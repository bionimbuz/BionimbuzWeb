package app.controllers;

import static app.common.OSClientHelper.getOSClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.identity.v3.Region;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.common.GlobalConstants;
import app.common.SystemConstants;
import app.models.Body;
import app.models.PluginComputingInstanceModel;
import app.models.PluginComputingRegionModel;
import app.models.PluginComputingZoneModel;

@RestController
public class ComputingController extends AbstractComputingController {

    /*
     * Overwritten Methods
     */

    @Override
    protected ResponseEntity<Body<PluginComputingInstanceModel>> createInstance(final String token,
            final String identity, final PluginComputingInstanceModel model) throws Exception {

        try {
            OSClient.OSClientV3 os = getOSClient(token);
            int size = os.compute().servers().list().size() + 1;
            String name = "bionimbuz-instance-openstack-" + size;

            ServerCreate sc = Builders.server().name(name).flavor("1").image("9c795320-417c-4b15-9494-918209b3edf7").build();
            Server server = os.compute().servers().boot(sc);

            waitInstanceCreation(os, server);

            server = os.compute().servers().get(server.getId());
            String ip = server.getAddresses().getAddresses().get("public").get(0).getAddr();
            final PluginComputingInstanceModel res = model;
            res.setName(name);
            res.setExternalIp(ip);
            return ResponseEntity.ok(Body.create(res));
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected ResponseEntity<Body<PluginComputingInstanceModel>> getInstance(final String token, final String identity,
            final String region, final String zone, final String instanceId) throws Exception {
        try {

            OSClient.OSClientV3 os = getOSClient(token);

            Server server = os.compute().servers().get(instanceId);
            if (server == null) {
                return new ResponseEntity<>(
                        Body.create(null),
                        HttpStatus.NOT_FOUND);
            }
            final PluginComputingInstanceModel model = new PluginComputingInstanceModel();
            this.updateInstanceModel(server, model);
            return ResponseEntity.ok(Body.create(model));
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected ResponseEntity<Body<Boolean>> deleteInstance(final String token, final String identity,
            final String region, final String zone, final String name) throws Exception {
        try {

            OSClient.OSClientV3 os = getOSClient(token);

            for (Server server : os.compute().servers().list()) {
                if (server.getName().equals(name)) {
                    if (os.compute().servers().delete(server.getId()).isSuccess()) {
                        return new ResponseEntity<>(Body.create(true), HttpStatus.OK);
                    }
                }
            }
            return new ResponseEntity<>(Body.create(false), HttpStatus.NOT_FOUND);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected ResponseEntity<Body<List<PluginComputingInstanceModel>>> listInstances(final String token,
            final String identity) throws Exception {
        try {

            OSClient.OSClientV3 os = getOSClient(token);

            List<? extends Server> servers = os.compute().servers().list();
            return ResponseEntity.ok(Body.create(serversToPluginModel(servers)));
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected ResponseEntity<Body<List<PluginComputingRegionModel>>> listRegions(final String token,
            final String identity) throws Exception {
        try {

            OSClient.OSClientV3 os = getOSClient(token);

            List<? extends Region> regionList = os.identity().regions().list();

            final List<PluginComputingRegionModel> res = regionsToRegionModel(regionList);
            return ResponseEntity.ok(Body.create(res));
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }


    @Override
    protected ResponseEntity<Body<List<PluginComputingZoneModel>>> listRegionZones(final String token,
            final String identity, final String name) throws Exception {
        final List<PluginComputingZoneModel> res = new ArrayList<>();
        res.add(createZoneModel());
        return ResponseEntity.ok(
                Body.create(res));
    }


    /*
     * Specific Class Methods
     */


    private boolean updateInstanceModel(final Server server, final PluginComputingInstanceModel model) {

        if (!server.getName().startsWith(GlobalConstants.BNZ_INSTANCE)) {
            return false;
        }

        model.setId(server.getId());
        model.setName(server.getName());
        model.setFlavorId(server.getFlavorId());
        model.setImageId(server.getImageId());
        model.setCreationDate(server.getCreated());

        return true;
    }

    private List<PluginComputingInstanceModel> serversToPluginModel(List<? extends Server> servers) {
        List<PluginComputingInstanceModel> models = new ArrayList<>();
        for(Server server : servers) {
            PluginComputingInstanceModel model = new PluginComputingInstanceModel();
            model.setName(server.getName());
            model.setFlavorId(server.getFlavorId());
            model.setImageId(server.getImageId());
            models.add(model);
        }
        return models;
    }

    private List<PluginComputingRegionModel> regionsToRegionModel(List<? extends org.openstack4j.model.identity.v3.Region> regions) {
        List<PluginComputingRegionModel> models = new ArrayList<>();
        for(Region region : regions) {
            PluginComputingRegionModel model = new PluginComputingRegionModel(region.getId());
            models.add(model);
        }
        return models;
    }

    public static PluginComputingZoneModel createZoneModel() {
        return new PluginComputingZoneModel(SystemConstants.PLUGIN_ZONE);
    }

    private void waitInstanceCreation(OSClient.OSClientV3 os, Server server) throws InterruptedException {
        int time = 0;
        while (time <= 10) {
            if (os.compute().servers().get(server.getId()).getStatus().name().equals("ACTIVE")) {
                break;
            }           
            TimeUnit.SECONDS.sleep(1);
            time++;
        }
    }

}

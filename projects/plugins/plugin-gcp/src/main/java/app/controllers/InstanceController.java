package app.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface;
import org.jclouds.googlecomputeengine.domain.Instance.NetworkInterface.AccessConfig;
import org.jclouds.googlecomputeengine.domain.NewInstance;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Zone;
import org.jclouds.googlecomputeengine.features.InstanceApi;
import org.jclouds.googlecomputeengine.features.ZoneApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.common.GlobalConstants;
import app.common.GoogleComputeEngineUtils;
import app.common.Pair;
import app.models.Body;
import app.models.PluginInstanceModel;

@RestController
public class InstanceController extends AbstractInstanceController {

    private static final int CREATION_ATTEMPTS = 3;

    /*
     * Overwritten Methods
     */

    @Override
    protected ResponseEntity<Body<List<PluginInstanceModel>>> createInstance(
            final String token,
            final String identity,
            final List<PluginInstanceModel> listModel) throws Exception {
        try (
             GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi(
                     identity,
                     token)) {
            final List<PluginInstanceModel> res = listModel;
            FirewallController.createRulesForInstances(googleApi, listModel);
            this.createInstances(googleApi, listModel);
            return ResponseEntity.ok(
                    Body.create(res));
        }
    }

    @Override
    protected ResponseEntity<Body<PluginInstanceModel>> getInstance(
            final String token,
            final String identity,
            final String zone,
            final String name) throws Exception {
        try (
             GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi(
                     identity,
                     token)) {
            final InstanceApi instanceApi = googleApi.instancesInZone(zone);
            final Instance instance = instanceApi.get(name);
            if (instance == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            final PluginInstanceModel model = new PluginInstanceModel();
            this.updateInstanceModel(instance, model);
            return ResponseEntity.ok(
                    Body.create(model));
        }
    }

    @Override
    protected ResponseEntity<Body<Void>> deleteInstance(
            final String token,
            final String identity,
            final String zone,
            final String name) throws Exception {
        try (
             GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi(
                     identity,
                     token)) {
            if (!this.deleteInstance(googleApi, zone, name)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Override
    protected ResponseEntity<Body<List<PluginInstanceModel>>> listInstances(
            final String token,
            final String identity) throws Exception {
        try (
             GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi(
                     identity,
                     token)) {
            final List<PluginInstanceModel> res = this.getInstances(googleApi);
            return ResponseEntity.ok(
                    Body.create(res));
        }
    }

    /*
     * Specific Class Methods
     */

    private List<PluginInstanceModel> getInstances(final GoogleComputeEngineApi googleApi) {

        final List<PluginInstanceModel> res = new ArrayList<>();
        final ZoneApi zoneApi = googleApi.zones();
        final Iterator<ListPage<Zone>> listPages = zoneApi.list();
        while (listPages.hasNext()) {
            final ListPage<Zone> zones = listPages.next();
            for (final Zone zone : zones) {
                final List<PluginInstanceModel> instances = this.getInstanceListForZone(googleApi, zone.name());
                if (!instances.isEmpty()) {
                    res.addAll(instances);
                }
            }
        }

        return res;
    }

    private List<PluginInstanceModel> getInstanceListForZone(
            final GoogleComputeEngineApi googleApi,
            final String zone) {

        final List<PluginInstanceModel> res = new ArrayList<>();
        final InstanceApi instanceApi = googleApi.instancesInZone(zone);
        final Iterator<ListPage<Instance>> listPages = instanceApi.list();
        while (listPages.hasNext()) {
            final ListPage<Instance> instances = listPages.next();
            for (final Instance instance : instances) {
                final PluginInstanceModel model = new PluginInstanceModel();
                if (this.updateInstanceModel(instance, model)) {
                    res.add(model);
                }
            }
        }

        return res;
    }

    private boolean updateInstanceModel(final Instance instance, final PluginInstanceModel model) {

        if (!instance.name().startsWith(GlobalConstants.BNZ_INSTANCE)) {
            return false;
        }

        String type = instance.machineType().toString();
        type = type.substring(type.lastIndexOf('/') + 1);

        model.setId(instance.id());
        model.setName(instance.name());
        model.setMachineType(type);
        model.setCreationDate(instance.creationTimestamp());

        final List<NetworkInterface> interfaces = instance.networkInterfaces();
        if (interfaces.size() <= 0) {
            return true;
        }

        final NetworkInterface netInterface = interfaces.get(0);
        model.setInternalIp(netInterface.networkIP());

        if (netInterface.accessConfigs().size() <= 0) {
            return true;
        }

        final AccessConfig accessConfig = netInterface.accessConfigs().get(0);
        model.setExternalIp(accessConfig.natIP());

        return true;
    }

    protected void createInstances(
            final GoogleComputeEngineApi googleApi,
            final List<PluginInstanceModel> instances) throws Exception {

        try {
            final List<Pair<PluginInstanceModel, Operation>> operations = new ArrayList<>();
            for (final PluginInstanceModel instance : instances) {
                operations.add(new Pair<>(instance, null));
            }

            final Iterator<Pair<PluginInstanceModel, Operation>> itOperations = operations.iterator();
            int instancesToCreate = instances.size();
            int attempts = CREATION_ATTEMPTS; // For concurrent users       
            while ((attempts-- > 0) && (instancesToCreate > 0)) {
                PluginInstanceModel model;
                Operation operation;
                final List<String> newNames = PluginInstanceModel.generateUniqueNames(
                        this.getInstances(googleApi),
                        instancesToCreate,
                        GlobalConstants.BNZ_INSTANCE);
                final Iterator<String> itNames = newNames.iterator();

                while (itOperations.hasNext() && itNames.hasNext()) {
                    final Pair<PluginInstanceModel, Operation> modelOperation = itOperations.next();
                    model = modelOperation.getLeft();
                    if (!model.getName().isEmpty()) {
                        continue;
                    }
                    model.setName(itNames.next());
                    modelOperation.setRight(
                            this.createInstance(googleApi, model));
                }

                instancesToCreate = 0;
                for (final Pair<PluginInstanceModel, Operation> modelOperation : operations) {
                    model = modelOperation.getLeft();
                    operation = modelOperation.getRight();
                    try {
                        GoogleComputeEngineUtils.waitOperation(googleApi, operation);
                    } catch (final Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        instancesToCreate++;
                        model.setName("");
                    }
                }
            }

            if (instancesToCreate > 0) {
                throw new Exception("Reached the number of attempts to create instances. Operation aborted.");
            }

            // Update instance informations (external and internal IP, etc.)
            for (final PluginInstanceModel model : instances) {
                final InstanceApi instanceApi = googleApi.instancesInZone(model.getZone());
                final Instance instance = instanceApi.get(model.getName());
                if (instance == null) {
                    throw new Exception("Instance created does not exist anymore.");
                }
                this.updateInstanceModel(instance, model);
            }
        } catch (final Exception e) {
            // Delete instances already created
            this.deleteInstances(googleApi, instances);
            throw e;
        }
    }

    private void deleteInstances(
            final GoogleComputeEngineApi googleApi,
            final List<PluginInstanceModel> instances) {

        for (final PluginInstanceModel instance : instances) {
            if (instance.getName().isEmpty()) {
                continue;
            }
            this.deleteInstance(googleApi, instance);
        }
    }

    private Operation createInstance(
            final GoogleComputeEngineApi googleApi,
            final PluginInstanceModel instance)
            throws Exception {

        final URI machineTypeURL = googleApi
                .machineTypesInZone(instance.getZone())
                .get(instance.getType()).selfLink();

        final InstanceApi instanceApi = googleApi.instancesInZone(instance.getZone());

        final URI networkURL = GoogleComputeEngineUtils
                .assertDefaultNetwork(googleApi);
        //        URI subnetworkURL = GoogleComputeEngineUtils
        //                .assertDefaultSubnetwork(googleApi, instance.getRegion());

        final NewInstance newInstance = NewInstance.create(
                instance.getName(),
                machineTypeURL,
                networkURL,
                null,
                instance.getImageUri());

        newInstance.metadata().put(
                GlobalConstants.META_STARTUP_SCRIPT,
                instance.getStartupScript());

        return instanceApi.create(newInstance);
    }

    private boolean deleteInstance(
            final GoogleComputeEngineApi googleApi,
            final PluginInstanceModel instance) {
        return this.deleteInstance(googleApi, instance.getZone(), instance.getName());
    }

    private boolean deleteInstance(
            final GoogleComputeEngineApi googleApi,
            final String zone,
            final String name) {

        final InstanceApi instanceApi = googleApi.instancesInZone(zone);
        final Instance instance = instanceApi.get(name);
        if (instance == null) {
            return false;
        }
        final Operation operation = instanceApi.delete(name);
        GoogleComputeEngineUtils.waitOperation(googleApi, operation);
        return true;
    }
}

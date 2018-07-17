package app.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.features.AvailabilityZoneAndRegionApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Optional;

import app.common.AWSEC2Utils;
import app.models.Body;
import app.models.PluginInstanceRegionModel;
import app.models.PluginInstanceZoneModel;

@RestController
public class InstanceRegionController extends AbstractInstanceRegionController{

    /*
     * Overwritten Methods
     */
    @Override
    protected ResponseEntity<Body<List<PluginInstanceRegionModel>>> listInstanceRegions(
            final String token,
            final String identity) throws Exception {
        try(EC2Api awsApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {
            List<PluginInstanceRegionModel> res = getRegions(awsApi);
            return ResponseEntity.ok(
                    Body.create(res));
        }
    }
    @Override
    protected ResponseEntity<Body<List<PluginInstanceZoneModel>>> listInstanceRegionsZones(
            final String token,
            final String identity,
            final String name) throws Exception {
        try(EC2Api awsApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {
            List<PluginInstanceZoneModel> res = getRegionZones(awsApi, name);
            return ResponseEntity.ok(
                    Body.create(res));
        }
    }

    /*
     * Specific Class Methods
     */
    public static List<PluginInstanceRegionModel> getRegions(final EC2Api awsApi){

        List<PluginInstanceRegionModel> res = new ArrayList<>();
        Optional<? extends AvailabilityZoneAndRegionApi> api =
                (Optional<? extends AvailabilityZoneAndRegionApi>)
                    awsApi.getAvailabilityZoneAndRegionApi();
        AvailabilityZoneAndRegionApi apiZone =
                api.get();
        Map<String, URI> regions =
                apiZone.describeRegions();
        for(String region : regions.keySet()) {
            res.add(new PluginInstanceRegionModel(region));
        }
        return res;
    }

    private List<PluginInstanceZoneModel> getRegionZones(
            final EC2Api awsApi,
            final String name){

        List<PluginInstanceZoneModel> res = new ArrayList<>();
        Optional<? extends AvailabilityZoneAndRegionApi> api =
                (Optional<? extends AvailabilityZoneAndRegionApi>)
                    awsApi.getAvailabilityZoneAndRegionApiForRegion(name);
        AvailabilityZoneAndRegionApi apiZone =
                api.get();
        Set<AvailabilityZoneInfo> zones
                = apiZone.describeAvailabilityZonesInRegion(name);

        for (AvailabilityZoneInfo availabilityZoneInfo : zones) {
            res.add(new PluginInstanceZoneModel(availabilityZoneInfo.getZone()));
        }
        return res;
    }
}

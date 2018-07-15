package app.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.aws.ec2.AWSEC2Api;
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
        try(AWSEC2Api googleApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {
            List<PluginInstanceRegionModel> res = getRegions(googleApi);
            return ResponseEntity.ok(
                    Body.create(res));
        }
    }
    @Override
    protected ResponseEntity<Body<List<PluginInstanceZoneModel>>> listInstanceRegionsZones(
            final String token,
            final String identity,
            final String name) throws Exception {
        try(AWSEC2Api googleApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {
            List<PluginInstanceZoneModel> res = getRegionZones(googleApi, name);
            return ResponseEntity.ok(
                    Body.create(res));
        }
    }

    /*
     * Specific Class Methods
     */
    private List<PluginInstanceRegionModel> getRegions(final AWSEC2Api awsApi){

        List<PluginInstanceRegionModel> res = new ArrayList<>();
        Optional<? extends AvailabilityZoneAndRegionApi> api =
                (Optional<? extends AvailabilityZoneAndRegionApi>)
                    awsApi.getAvailabilityZoneAndRegionApi();
        AvailabilityZoneAndRegionApi apiZone =
                api.get();
        Map<String, URI> regions =
                apiZone.describeRegions();
        for(String key : regions.keySet()) {
            res.add(new PluginInstanceRegionModel(key));
        }
        return res;
    }

    private List<PluginInstanceZoneModel> getRegionZones(
            final AWSEC2Api awsApi,
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

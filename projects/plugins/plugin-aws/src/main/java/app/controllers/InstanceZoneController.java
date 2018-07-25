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
import app.models.PluginInstanceZoneModel;

@RestController
public class InstanceZoneController extends AbstractInstanceZoneController{

    /*
     * Overwritten Methods
     */
    @Override
    protected ResponseEntity<Body<List<PluginInstanceZoneModel>>> listZones(
            final String token,
            final String identity) throws Exception {
        try(EC2Api awsApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {
            List<PluginInstanceZoneModel> res = getZones(awsApi);
            return ResponseEntity.ok(
                    Body.create(res));
        }
    }

    /*
     * Specific Class Methods
     */

    private List<PluginInstanceZoneModel> getZones(
            final EC2Api awsApi){

        List<PluginInstanceZoneModel> res = new ArrayList<>();
        Optional<? extends AvailabilityZoneAndRegionApi> api =
                (Optional<? extends AvailabilityZoneAndRegionApi>)
                    awsApi.getAvailabilityZoneAndRegionApi();
        AvailabilityZoneAndRegionApi apiZone =
                api.get();
        Map<String, URI> regions =
                apiZone.describeRegions();

        for(String region : regions.keySet()) {
            Set<AvailabilityZoneInfo> zones
                    = apiZone.describeAvailabilityZonesInRegion(region);
            for (AvailabilityZoneInfo availabilityZoneInfo : zones) {
                res.add(new PluginInstanceZoneModel(availabilityZoneInfo.getZone()));
            }
        }

        return res;
    }
}

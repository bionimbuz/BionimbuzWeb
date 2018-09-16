package app.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.InstanceStateChange;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.features.AvailabilityZoneAndRegionApi;
import org.jclouds.ec2.features.InstanceApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Optional;

import app.common.AWSEC2Utils;
import app.common.FirewallUtils;
import app.common.utils.StringUtils;
import app.models.Body;
import app.models.PluginComputingInstanceModel;
import app.models.PluginComputingRegionModel;
import app.models.PluginComputingZoneModel;

@RestController
public class ComputingController extends AbstractComputingController {

    
    private static Long MAX_ATTEMPTS = 10l;
    /*
     * Overwritten Methods
     */

    @Override
    protected ResponseEntity<Body<PluginComputingInstanceModel>> createInstance(
            final String token,
            final String identity,
            final PluginComputingInstanceModel model) throws Exception {
        try(EC2Api awsApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {            
            createInstance(awsApi, model);
            if(model.getId() != null && !model.getId().isEmpty()) {
                try {
                    FirewallUtils.createRulesForInstance(awsApi, model);
                } catch(final Exception e) {
                    e.printStackTrace();
                }
            }
            return ResponseEntity.ok(
                    Body.create(model));            
        }
    }     

    @Override
    protected ResponseEntity<Body<Boolean>> deleteInstance(
            final String token,
            final String identity,
            final String region,
            final String zone,
            final String name) throws Exception {
        
        try(EC2Api awsApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {            
            InstanceApi instanceApi =
                    awsApi.getInstanceApi().get();
            Set<? extends InstanceStateChange> instanceStateSet = 
                    instanceApi.terminateInstancesInRegion(region, name);            
            InstanceStateChange instanceStateChange = 
                    instanceStateSet.iterator().next();            
            InstanceState state = 
                    instanceStateChange.getCurrentState();           
            
            boolean terminated = 
                    state == InstanceState.TERMINATED 
                    || state == InstanceState.SHUTTING_DOWN 
                    || state == InstanceState.STOPPING 
                    || state == InstanceState.STOPPED 
                    || state == InstanceState.SHUTTING_DOWN;
            
            return ResponseEntity.ok(
                    Body.create(terminated));            
        }
    }  

    @Override
    protected ResponseEntity<Body<PluginComputingInstanceModel>> getInstance(
            final String token,
            final String identity,
            final String region,
            final String zone,
            final String name) throws Exception {

        try(EC2Api awsApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {
            InstanceApi instanceApi =
                    awsApi.getInstanceApi().get();            

            Set<? extends Reservation<? extends RunningInstance>> reservationSet = 
                    instanceApi.describeInstancesInRegion(region, name);
            Reservation<? extends RunningInstance> runningInstance = 
                    reservationSet.iterator().next();            
            if(runningInstance == null) {
                return new ResponseEntity<>(
                        Body.create(null),
                        HttpStatus.NOT_FOUND);
            }
            
            PluginComputingInstanceModel instance = new PluginComputingInstanceModel();
            updateInstanceModel(instance, runningInstance);
            return ResponseEntity.ok(
                    Body.create(instance));   
        }
    }

    @Override
    protected ResponseEntity<Body<List<PluginComputingInstanceModel>>> listInstances(
            final String token,
            final String identity) throws Exception {

        try(EC2Api awsApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {
            List<PluginComputingInstanceModel> res = new ArrayList<>();
            InstanceApi instanceApi =
                    awsApi.getInstanceApi().get();  
            List<PluginComputingRegionModel> regions = getRegions(awsApi);
            for (PluginComputingRegionModel region : regions) {
                Set<? extends Reservation<? extends RunningInstance>> reservationSet = 
                        instanceApi.describeInstancesInRegion(region.getName());
                for (Reservation<? extends RunningInstance> runningInstance : reservationSet) {
                    PluginComputingInstanceModel instance = new PluginComputingInstanceModel();
                    updateInstanceModel(instance, runningInstance);
                    res.add(instance);
                }
            }

            return ResponseEntity.ok(
                    Body.create(res));  
        }
    }
    
    @Override
    protected ResponseEntity<Body<List<PluginComputingRegionModel>>> listRegions(
            final String token,
            final String identity) throws Exception {
        try(EC2Api awsApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {
            List<PluginComputingRegionModel> res = getRegions(awsApi);
            return ResponseEntity.ok(
                    Body.create(res));
        }
    }
    
    @Override
    protected ResponseEntity<Body<List<PluginComputingZoneModel>>> listRegionZones(
            final String token,
            final String identity,
            final String name) throws Exception {
        try(EC2Api awsApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {
            List<PluginComputingZoneModel> res = getRegionZones(awsApi, name);
            return ResponseEntity.ok(
                    Body.create(res));
        }
    }
    
    /*
     * Specific Class Methods
     */
    
    private void createInstance(EC2Api awsApi, PluginComputingInstanceModel instance) {
        
        Image image = 
                ImageController.searchFirstImageWithNameFilter(
                        awsApi,
                        instance.getImageUrl());
        InstanceApi instanceApi =
                awsApi.getInstanceApi().get();
        Reservation<? extends RunningInstance> runningInstance = 
                instanceApi.runInstancesInRegion(
                instance.getRegion(), 
                instance.getZone(), 
                image.getId(), 
                1, 1, 
                AWSRunInstancesOptions.Builder
                    .asType(instance.getType())
                    .withUserData(instance.getStartupScript().getBytes()));   
        
        
        for(int i=0; i < MAX_ATTEMPTS; i++) {
            updateInstanceModel(instance, runningInstance);
            if(instance.getId() == null || instance.getId().isEmpty())
                return;
            
            if(!StringUtils.isEmpty(instance.getExternalIp())) {
                return;
            }
            
            Set<? extends Reservation<? extends RunningInstance>> remoteRunningInstance = 
                    instanceApi.describeInstancesInRegion(
                    instance.getRegion(), instance.getId());        
            updateInstanceModel(
                    instance, 
                    remoteRunningInstance.iterator().next());

            if(!StringUtils.isEmpty(instance.getExternalIp())) {
                return;
            }
            
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updateInstanceModel(
            PluginComputingInstanceModel instance, 
            Reservation<? extends RunningInstance> runningInstance) { 
        if(runningInstance == null)
            return;
        RunningInstance createdInstance = 
                runningInstance.iterator().next();
        if(createdInstance == null)
            return;        
        instance.setId(createdInstance.getId());
        instance.setName(createdInstance.getId());
        instance.setCreationDate(createdInstance.getLaunchTime());
        instance.setInternalIp(createdInstance.getPrivateIpAddress());
        instance.setExternalIp(createdInstance.getIpAddress());
    } 
    
    public static List<PluginComputingRegionModel> getRegions(final EC2Api awsApi){

        List<PluginComputingRegionModel> res = new ArrayList<>();
        Optional<? extends AvailabilityZoneAndRegionApi> api =
                (Optional<? extends AvailabilityZoneAndRegionApi>)
                    awsApi.getAvailabilityZoneAndRegionApi();
        AvailabilityZoneAndRegionApi apiZone =
                api.get();
        Map<String, URI> regions =
                apiZone.describeRegions();
        for(String region : regions.keySet()) {
            res.add(new PluginComputingRegionModel(region));
        }
        return res;
    }

    private List<PluginComputingZoneModel> getRegionZones(
            final EC2Api awsApi,
            final String name){

        List<PluginComputingZoneModel> res = new ArrayList<>();
        Optional<? extends AvailabilityZoneAndRegionApi> api =
                (Optional<? extends AvailabilityZoneAndRegionApi>)
                    awsApi.getAvailabilityZoneAndRegionApiForRegion(name);
        AvailabilityZoneAndRegionApi apiZone =
                api.get();
        Set<AvailabilityZoneInfo> zones
                = apiZone.describeAvailabilityZonesInRegion(name);

        for (AvailabilityZoneInfo availabilityZoneInfo : zones) {
            res.add(new PluginComputingZoneModel(availabilityZoneInfo.getZone()));
        }
        return res;
    }
}

package app.controllers;

import java.util.List;
import java.util.Set;

import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.features.InstanceApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.common.AWSEC2Utils;
import app.common.FirewallUtils;
import app.models.Body;
import app.models.PluginInstanceModel;

@RestController
public class InstanceController extends AbstractInstanceController {

    /*
     * Overwritten Methods
     */

    @Override
    protected ResponseEntity<Body<List<PluginInstanceModel>>> createInstance(
            final String token,
            final String identity,
            final List<PluginInstanceModel> listModel) throws Exception {
        try(EC2Api awsApi =
                AWSEC2Utils.createApi(
                        identity,
                        token)) {            
            for (PluginInstanceModel instance : listModel) {
                createInstance(awsApi, instance);
                if(instance.getId() != null && !instance.getId().isEmpty()) {
                    try {
                        FirewallUtils.createRulesForInstance(awsApi, instance);
                    } catch(final Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return ResponseEntity.ok(
                    Body.create(listModel));            
        }
    }       

    @Override
    protected ResponseEntity<Body<PluginInstanceModel>> getInstance(
            final String token,
            final String identity,
            final String zone,
            final String name) throws Exception {

        return null;
    }

    @Override
    protected ResponseEntity<Body<Boolean>> deleteInstance(
            final String token,
            final String identity,
            final String zone,
            final String name) throws Exception {

        return null;
    }

    @Override
    protected ResponseEntity<Body<List<PluginInstanceModel>>> listInstances(
            final String token,
            final String identity) throws Exception {

        return null;
    }
    
    private void createInstance(EC2Api awsApi, PluginInstanceModel instance) {
        
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
        
        updateInstanceModel(instance, runningInstance);
        if(instance.getId() == null || instance.getId().isEmpty())
            return;
        
        Set<? extends Reservation<? extends RunningInstance>> remoteRunningInstance = 
                instanceApi.describeInstancesInRegion(
                instance.getRegion(), instance.getId());        
        updateInstanceModel(
                instance, 
                remoteRunningInstance.iterator().next());
    }
    
    private void updateInstanceModel(
            PluginInstanceModel instance, 
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
}
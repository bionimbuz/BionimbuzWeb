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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.common.GoogleComputeEngineUtils;
import app.common.SystemConstants;
import app.models.InstanceCreationModel;
import app.models.InstanceModel;
import app.models.ZoneModel;

@RestController
public class InstanceController {

    /*
     * Temp Methods
     */
//    @RequestMapping(path = "/instance/delete", method = RequestMethod.GET)
//    public Object delete() {               
//        return instance(SystemConstants.BNZ_INSTANCE, "us-east1-b");
//    }
//    
//    @RequestMapping(path = "/instance/create", method = RequestMethod.GET)
//    public Object create() {
//        
//        List<InstanceCreationModel> instances = new ArrayList<>();        
//        
//        InstanceCreationModel instance = new InstanceCreationModel();
//        instance.setImageUrl("https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1604-xenial-v20170919");
//        instance.setStartupScript("apt-get update && apt-get install -y apache2 && hostname > /var/www/index.html");
//        instance.setType("f1-micro");
//        instance.setRegion("us-east1");
//        instance.setZone("us-east1-b");
//        
//        instances.add(instance);
//        
//        return instance(instances);
//    }

    /*
     * Controller Methods
     */
    
    @RequestMapping(path = "/instance", method = RequestMethod.POST)
    public ResponseEntity<?> instance(
            @RequestParam(value = "instance") List<InstanceCreationModel> instances) {
        
        try {            
            GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi();  
            ArrayList<URI> createdInstances = createInstances(googleApi, instances);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity
		            .status(HttpStatus.INTERNAL_SERVER_ERROR)
		            .body(e.getMessage());
        }
    }
    
    @RequestMapping(path = "/instance", method = RequestMethod.DELETE)
    public ResponseEntity<?> instance(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "zone") String zone) {

        GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi(); 
        InstanceApi instanceApi = googleApi.instancesInZone(zone);
        
        if(instanceApi.get(name) != null) {            
            Operation operation = instanceApi.delete(name);
            GoogleComputeEngineUtils.waitOperation(googleApi, operation);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @RequestMapping(path = "/instances", method = RequestMethod.GET)
    public List<ZoneModel> instances() {
        GoogleComputeEngineApi googleApi = GoogleComputeEngineUtils.createApi(); 
        List<ZoneModel> res = getZonesWithInstances(googleApi);
        return res;        
    }

    /*
     * Class Methods
     */
    
    private List<ZoneModel> getZonesWithInstances(final GoogleComputeEngineApi googleApi){
        
        List<ZoneModel> res = new ArrayList<>();        
        ZoneApi zoneApi = googleApi.zones();
        Iterator<ListPage<Zone>> listPages = zoneApi.list();
        while (listPages.hasNext()) {
            ListPage<Zone> zones = listPages.next();
            for (Zone zone : zones) {
                
                List<InstanceModel> instances = 
                        getInstanceListForZone(googleApi, zone.name());    
                if(!instances.isEmpty())
                    res.add(new ZoneModel(zone.name(), instances));
            }
        }        
        
        return res;        
    }
    
    private List<InstanceModel> getInstanceListForZone(
            final GoogleComputeEngineApi googleApi, 
            final String zone) {        
        
        List<InstanceModel> res = new ArrayList<>();
        InstanceApi instanceApi = googleApi.instancesInZone(zone);
        Iterator<ListPage<Instance>> listPages = instanceApi.list();
        while(listPages.hasNext()){
            ListPage<Instance> instances = listPages.next();
            for (Instance instance : instances) {
                InstanceModel model = createInstanceModel(instance);
                if(model != null) {
                    res.add(model);
                }
            }            
        }
        
        return res;
    }

    private InstanceModel createInstanceModel(final Instance instance) {
        
        if(!instance.name().startsWith(SystemConstants.BNZ_INSTANCE))
            return null;;   
        
        InstanceModel instanceModel = new InstanceModel(
                instance.id(),
                instance.name(),
                instance.machineType().toString(),
                instance.creationTimestamp());
        
        List<NetworkInterface> interfaces = instance.networkInterfaces();
        if(interfaces.size() <= 0) 
            return instanceModel;               
            
        NetworkInterface netInterface = 
                interfaces.get(0); 
        instanceModel.setInternalIp(netInterface.networkIP());
        
        if(netInterface.accessConfigs().size() <= 0)
            return instanceModel;        
        
        AccessConfig accessConfig = 
                netInterface.accessConfigs().get(0);
        instanceModel.setExternalIp(accessConfig.natIP());
        
        return instanceModel;        
    }

    private ArrayList<URI> createInstances(
            GoogleComputeEngineApi googleApi,
            List<InstanceCreationModel> instances)
                    throws Exception {

        ArrayList<Operation> operations = new ArrayList<Operation>();
        
        for (InstanceCreationModel instance : instances) {        
            operations.add(createInstance(googleApi, instance));            
        }

        ArrayList<URI> createdInstances = new ArrayList<URI>();
        for (Operation operation : operations) {
            GoogleComputeEngineUtils.waitOperation(googleApi, operation);
            createdInstances.add(operation.targetLink());
        }
        return createdInstances;
    }    
    
    private Operation createInstance(
            GoogleComputeEngineApi googleApi,
            InstanceCreationModel instance)
                    throws Exception {
        
        URI machineTypeURL = googleApi
                    .machineTypesInZone(instance.getZone())
                    .get(instance.getType()).selfLink();

        InstanceApi instanceApi = googleApi.instancesInZone(instance.getZone());        

        URI networkURL = GoogleComputeEngineUtils.assertDefaultNetwork(googleApi);
                  
        NewInstance newInstance = 
                NewInstance.create(
                    SystemConstants.BNZ_INSTANCE, 
                    machineTypeURL, 
                    networkURL,
                    instance.getImageUri());
        
        newInstance.metadata().put(
                SystemConstants.META_STARTUP_SCRIPT, 
                instance.getStartupScript());
                        
        return instanceApi.create(newInstance);
    }
}

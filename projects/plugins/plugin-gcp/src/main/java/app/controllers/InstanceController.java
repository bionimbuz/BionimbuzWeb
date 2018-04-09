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
public class InstanceController extends AbstractInstanceController{ 
    private static final int CREATION_ATTEMPTS = 3;

    /*
     * Overwritten Methods
     */
    
    @Override
    protected ResponseEntity<Body<List<PluginInstanceModel>>> createInstance(
            final String token, 
            final String identity,
            List<PluginInstanceModel> listModel) throws Exception {          
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
                        identity, 
                        token)) { 
            List<PluginInstanceModel> res = listModel;
            FirewallController.createRulesForInstances(googleApi, listModel);
            createInstances(googleApi, listModel);        
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
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
                        identity, 
                        token)) { 
            InstanceApi instanceApi = googleApi.instancesInZone(zone);                
            Instance instance = instanceApi.get(name);
            if(instance == null) { 
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            }
            
            PluginInstanceModel model = new PluginInstanceModel();
            updateInstanceModel(instance, model);                  
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
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
                        identity, 
                        token)) {          
            if(!deleteInstance(googleApi, zone, name)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            }           
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Override
    protected ResponseEntity<Body<List<PluginInstanceModel>>> listInstances(
            final String token, 
            final String identity) throws Exception {          
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
                        identity, 
                        token)) { 
            List<PluginInstanceModel> res = getInstances(googleApi);     
            return ResponseEntity.ok(
                    Body.create(res));
        }
    }

    /*
     * Specific Class Methods
     */
    
    private List<PluginInstanceModel> getInstances(final GoogleComputeEngineApi googleApi){
        
        List<PluginInstanceModel> res = new ArrayList<>();        
        ZoneApi zoneApi = googleApi.zones();
        Iterator<ListPage<Zone>> listPages = zoneApi.list();
        while (listPages.hasNext()) {
            ListPage<Zone> zones = listPages.next();
            for (Zone zone : zones) {                
                List<PluginInstanceModel> instances = 
                        getInstanceListForZone(googleApi, zone.name());    
                if(!instances.isEmpty())
                    res.addAll(instances);
            }
        }        
        
        return res;        
    }
    
    private List<PluginInstanceModel> getInstanceListForZone(
            final GoogleComputeEngineApi googleApi, 
            final String zone) {        
        
        List<PluginInstanceModel> res = new ArrayList<>();
        InstanceApi instanceApi = googleApi.instancesInZone(zone);
        Iterator<ListPage<Instance>> listPages = instanceApi.list();
        while(listPages.hasNext()){
            ListPage<Instance> instances = listPages.next();
            for (Instance instance : instances) {
                PluginInstanceModel model = new PluginInstanceModel();
                if(updateInstanceModel(instance, model)) {
                    res.add(model);
                }
            }            
        }
        
        return res;
    }

    private boolean updateInstanceModel(final Instance instance, PluginInstanceModel model) {
        
        if(!instance.name().startsWith(GlobalConstants.BNZ_INSTANCE))
            return false;
       
        String type = instance.machineType().toString();
        type = type.substring(type.lastIndexOf('/') + 1);
                
        model.setId(instance.id());
        model.setName(instance.name());
        model.setMachineType(type);
        model.setCreationDate(instance.creationTimestamp());
        
        List<NetworkInterface> interfaces = instance.networkInterfaces();
        if(interfaces.size() <= 0) 
            return true;               
            
        NetworkInterface netInterface = 
                interfaces.get(0); 
        model.setInternalIp(netInterface.networkIP());
        
        if(netInterface.accessConfigs().size() <= 0)
            return true;        
        
        AccessConfig accessConfig = 
                netInterface.accessConfigs().get(0);
        model.setExternalIp(accessConfig.natIP());
        
        return true;        
    }
    
    protected void createInstances(
            GoogleComputeEngineApi googleApi,
            List<PluginInstanceModel> instances) throws Exception{
        
        try {            
            List<Pair<PluginInstanceModel, Operation>> operations = new ArrayList<>();
            for (PluginInstanceModel instance : instances) {
                operations.add(new Pair<>(instance, null));   
            }                
    
            Iterator<Pair<PluginInstanceModel, Operation>> itOperations = operations.iterator(); 
            int instancesToCreate = instances.size();   
            int attempts = CREATION_ATTEMPTS; // For concurrent users       
            while((attempts-- > 0) && (instancesToCreate > 0)) { 
                PluginInstanceModel model;
                Operation operation;                     
                List<String> newNames = PluginInstanceModel.generateUniqueNames(
                        getInstances(googleApi), 
                        instancesToCreate, 
                        GlobalConstants.BNZ_INSTANCE);          
                Iterator<String> itNames = newNames.iterator();
                
                while(itOperations.hasNext() && itNames.hasNext()) {
                    Pair<PluginInstanceModel, Operation> modelOperation = itOperations.next();                
                    model = modelOperation.getLeft();
                    if(!model.getName().isEmpty())
                        continue;                
                    model.setName(itNames.next());
                    modelOperation.setRight(
                            createInstance(googleApi, model));
                }
                
                instancesToCreate = 0;
                for (Pair<PluginInstanceModel, Operation> modelOperation : operations) {
                    model = modelOperation.getLeft();
                    operation = modelOperation.getRight();
                    try {
        	            GoogleComputeEngineUtils.waitOperation(googleApi, operation);    	            
                    } catch(Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        instancesToCreate++;
                        model.setName("");
                    }        
                }
            }

            if(instancesToCreate > 0) {
                throw new Exception("Reached the number of attempts to create instances. Operation aborted.");
            }   

            // Update instance informations (external and internal IP, etc.)
            for (PluginInstanceModel model : instances) {
                InstanceApi instanceApi = googleApi.instancesInZone(model.getZone());               
                Instance instance = instanceApi.get(model.getName());
                if(instance == null) {   
                    throw new Exception("Instance created does not exist anymore.");
                }  
                updateInstanceModel(instance, model);
            }
        }
        catch(Exception e) {
            // Delete instances already created
            deleteInstances(googleApi, instances);
            throw e;
        }        
    }      
    
    private void deleteInstances(
            GoogleComputeEngineApi googleApi,
            final List<PluginInstanceModel> instances) {
        
        for (PluginInstanceModel instance : instances) {
            if(instance.getName().isEmpty()) {
                continue;
            }
            deleteInstance(googleApi, instance);
        }    
    }
    
    private Operation createInstance(
            GoogleComputeEngineApi googleApi,
            PluginInstanceModel instance)
                    throws Exception {
        
        URI machineTypeURL = googleApi
                    .machineTypesInZone(instance.getZone())
                    .get(instance.getType()).selfLink();

        InstanceApi instanceApi = googleApi.instancesInZone(instance.getZone());        

        URI networkURL = GoogleComputeEngineUtils
                .assertDefaultNetwork(googleApi);
        URI subnetworkURL = GoogleComputeEngineUtils
                .assertDefaultSubnetwork(googleApi, instance.getRegion());
                  
        NewInstance newInstance = 
                NewInstance.create(
                    instance.getName(), 
                    machineTypeURL, 
                    networkURL,
                    subnetworkURL,
                    instance.getImageUri());
        
        newInstance.metadata().put(
                GlobalConstants.META_STARTUP_SCRIPT, 
                instance.getStartupScript());
                        
        return instanceApi.create(newInstance);
    }
    
    private boolean deleteInstance(
            GoogleComputeEngineApi googleApi, 
            final PluginInstanceModel instance) {
        return deleteInstance(googleApi, instance.getZone(), instance.getName());
    }

    private boolean deleteInstance(
            GoogleComputeEngineApi googleApi, 
            final String zone, 
            final String name) {
        
        InstanceApi instanceApi = googleApi.instancesInZone(zone);                
        Instance instance = instanceApi.get(name);
        if(instance == null) {   
            return false; 
        }                  
        Operation operation = instanceApi.delete(name);
        GoogleComputeEngineUtils.waitOperation(googleApi, operation);        
        return true;
    }
}

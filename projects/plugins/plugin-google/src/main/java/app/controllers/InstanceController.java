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
import app.models.CredentialModel;
import app.models.InstanceModel;

@RestController
public class InstanceController extends AbstractInstanceController{ 
    private static final int CREATION_ATTEMPTS = 3;

    /*
     * Overwritten Methods
     */
    
    @Override
    protected ResponseEntity<?> createInstance(
            CredentialModel<List<InstanceModel>> credential) {        
        try(GoogleComputeEngineApi googleApi = 
                    GoogleComputeEngineUtils.createApi(
                            credential.getIdentity(), 
                            credential.getCredential())) {
            List<InstanceModel> res = credential.getModel();
            createInstances(googleApi, res);            
            return ResponseEntity
		            .status(HttpStatus.OK)
		            .body(res); 
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity
		            .status(HttpStatus.INTERNAL_SERVER_ERROR)
		            .body(e.getMessage());
        }
    }

    @Override
    protected ResponseEntity<?> getInstance(
            final String zone,
            final String name,
            CredentialModel<Void> credential) {
        try(GoogleComputeEngineApi googleApi = 
                    GoogleComputeEngineUtils.createApi(
                            credential.getIdentity(), 
                            credential.getCredential())) {
            InstanceApi instanceApi = googleApi.instancesInZone(zone);                
            Instance instance = instanceApi.get(name);
            if(instance == null) { 
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            }
            
            InstanceModel model = new InstanceModel();
            updateInstanceModel(instance, model);            
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(model); 
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @Override
    protected ResponseEntity<?> deleteInstance(
            final String zone,
            final String name,
            CredentialModel<Void> credential) {       
        try(GoogleComputeEngineApi googleApi = 
                    GoogleComputeEngineUtils.createApi(
                            credential.getIdentity(), 
                            credential.getCredential())) {          
            if(!deleteInstance(googleApi, zone, name)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            }           
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @Override
    protected ResponseEntity<?> listInstances(
            CredentialModel<Void> credential) {        
        try(GoogleComputeEngineApi googleApi = 
                    GoogleComputeEngineUtils.createApi(
                            credential.getIdentity(), 
                            credential.getCredential())) {
            List<InstanceModel> res = getInstances(googleApi);         
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(res);    
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    /*
     * Specific Class Methods
     */
    
    private List<InstanceModel> getInstances(final GoogleComputeEngineApi googleApi){
        
        List<InstanceModel> res = new ArrayList<>();        
        ZoneApi zoneApi = googleApi.zones();
        Iterator<ListPage<Zone>> listPages = zoneApi.list();
        while (listPages.hasNext()) {
            ListPage<Zone> zones = listPages.next();
            for (Zone zone : zones) {                
                List<InstanceModel> instances = 
                        getInstanceListForZone(googleApi, zone.name());    
                if(!instances.isEmpty())
                    res.addAll(instances);
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
                InstanceModel model = new InstanceModel();
                if(updateInstanceModel(instance, model)) {
                    res.add(model);
                }
            }            
        }
        
        return res;
    }

    private boolean updateInstanceModel(final Instance instance, InstanceModel model) {
        
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
            List<InstanceModel> instances) throws Exception{
        
        try {
            List<Pair<InstanceModel, Operation>> operations = new ArrayList<>();
            for (InstanceModel instance : instances) {
                operations.add(new Pair<>(instance, null));   
            }                
    
            Iterator<Pair<InstanceModel, Operation>> itOperations = operations.iterator(); 
            int instancesToCreate = instances.size();   
            int attempts = CREATION_ATTEMPTS; // For concurrent users       
            while((attempts-- > 0) && (instancesToCreate > 0)) { 
                InstanceModel model;
                Operation operation;                     
                List<String> newNames = InstanceModel.generateUniqueNames(
                        getInstances(googleApi), 
                        instancesToCreate, 
                        GlobalConstants.BNZ_INSTANCE);          
                Iterator<String> itNames = newNames.iterator();
                
                while(itOperations.hasNext() && itNames.hasNext()) {
                    Pair<InstanceModel, Operation> modelOperation = itOperations.next();                
                    model = modelOperation.getLeft();
                    if(!model.getName().isEmpty())
                        continue;                
                    model.setName(itNames.next());
                    modelOperation.setRight(
                            createInstance(googleApi, model));
                }
                
                instancesToCreate = 0;
                for (Pair<InstanceModel, Operation> modelOperation : operations) {
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
            for (InstanceModel model : instances) {
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
            final List<InstanceModel> instances) {
        
        for (InstanceModel instance : instances) {
            if(instance.getName().isEmpty()) {
                continue;
            }
            deleteInstance(googleApi, instance);
        }    
    }
    
    private Operation createInstance(
            GoogleComputeEngineApi googleApi,
            InstanceModel instance)
                    throws Exception {
        
        URI machineTypeURL = googleApi
                    .machineTypesInZone(instance.getZone())
                    .get(instance.getType()).selfLink();

        InstanceApi instanceApi = googleApi.instancesInZone(instance.getZone());        

        URI networkURL = GoogleComputeEngineUtils.assertDefaultNetwork(googleApi);
                  
        NewInstance newInstance = 
                NewInstance.create(
                    instance.getName(), 
                    machineTypeURL, 
                    networkURL,
                    instance.getImageUri());
        
        newInstance.metadata().put(
                GlobalConstants.META_STARTUP_SCRIPT, 
                instance.getStartupScript());
                        
        return instanceApi.create(newInstance);
    }
    
    private boolean deleteInstance(
            GoogleComputeEngineApi googleApi, 
            final InstanceModel instance) {
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

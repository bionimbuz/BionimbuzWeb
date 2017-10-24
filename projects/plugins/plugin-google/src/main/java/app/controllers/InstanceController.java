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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.common.GoogleComputeEngineUtils;
import app.common.Routes;
import app.common.SystemConstants;
import app.models.CredentialModel;
import app.models.InstanceCreationModel;
import app.models.InstanceModel;
import app.models.ZoneModel;

@RestController
public class InstanceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceController.class);  

    /*
     * Controller Methods
     */
    
    @RequestMapping(path = Routes.INSTANCE, method = RequestMethod.POST)
    public ResponseEntity<?> createInstance(
            @RequestBody CredentialModel<List<InstanceCreationModel>> credential) {        
        try {            
            GoogleComputeEngineApi googleApi = 
                    GoogleComputeEngineUtils.createApi(credential.getCredential()); 
            ArrayList<URI> createdInstances = createInstances(googleApi, credential.getModel());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity
		            .status(HttpStatus.INTERNAL_SERVER_ERROR)
		            .body(e.getMessage());
        }
    }
    
    @RequestMapping(path = Routes.INSTANCE + "/{zone}" + "/{name}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteInstance(
            @PathVariable(value = "zone") String zone,
            @PathVariable(value = "name") String name,
            @RequestBody CredentialModel<Void> credential) {       
        try {
            GoogleComputeEngineApi googleApi = 
                    GoogleComputeEngineUtils.createApi(credential.getCredential()); 
            InstanceApi instanceApi = googleApi.instancesInZone(zone);

            Instance instance = instanceApi.get(name);
            if(instance == null) {   
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            }
                      
            Operation operation = instanceApi.delete(name);
            GoogleComputeEngineUtils.waitOperation(googleApi, operation);            
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
    
    @RequestMapping(path = Routes.INSTANCES, method = RequestMethod.POST)
    public ResponseEntity<?> listInstances(
            @RequestBody CredentialModel<Void> credential) {        
        try {
            GoogleComputeEngineApi googleApi = 
                    GoogleComputeEngineUtils.createApi(credential.getCredential()); 
            List<ZoneModel> res = getZonesWithInstances(googleApi);         
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
        List<ZoneModel> listZones = getZonesWithInstances(googleApi);
        List<String> newNames = InstanceModel.generateUniqueNames(listZones, instances.size(), SystemConstants.BNZ_INSTANCE);        
        
        Iterator<InstanceCreationModel> itModel = instances.iterator();
        Iterator<String> itNames = newNames.iterator();

        InstanceCreationModel model;
        while(itModel.hasNext() && itNames.hasNext()) {
            model = itModel.next();
            model.setName(itNames.next());
            Operation operation = createInstance(googleApi, model);
            operations.add(operation);    
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
                    instance.getName(), 
                    machineTypeURL, 
                    networkURL,
                    instance.getImageUri());
        
        newInstance.metadata().put(
                SystemConstants.META_STARTUP_SCRIPT, 
                instance.getStartupScript());
                        
        return instanceApi.create(newInstance);
    }
}

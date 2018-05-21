package app.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Zone;
import org.jclouds.googlecomputeengine.features.ZoneApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.common.GoogleComputeEngineUtils;
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
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
                        identity, 
                        token)) { 
            List<PluginInstanceZoneModel> res = getZones(googleApi);     
            return ResponseEntity.ok(
                    Body.create(res));
        }
    }

    /*
     * Specific Class Methods
     */    
    private List<PluginInstanceZoneModel> getZones(final GoogleComputeEngineApi googleApi){
        
        List<PluginInstanceZoneModel> res = new ArrayList<>();        
        ZoneApi api = googleApi.zones();
        Iterator<ListPage<Zone>> listPages = api.list();
        while (listPages.hasNext()) {
            ListPage<Zone> zones = listPages.next();
            for (Zone zone : zones) {      
                res.add(new PluginInstanceZoneModel(zone.name()));
            }
        }        
        
        return res;        
    }
}

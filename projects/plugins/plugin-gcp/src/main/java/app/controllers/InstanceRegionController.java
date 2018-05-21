package app.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jclouds.googlecloud.domain.ListPage;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Region;
import org.jclouds.googlecomputeengine.domain.Zone;
import org.jclouds.googlecomputeengine.features.RegionApi;
import org.jclouds.googlecomputeengine.features.ZoneApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.common.GoogleComputeEngineUtils;
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
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
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
        try(GoogleComputeEngineApi googleApi = 
                GoogleComputeEngineUtils.createApi(
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
    private List<PluginInstanceRegionModel> getRegions(final GoogleComputeEngineApi googleApi){
        
        List<PluginInstanceRegionModel> res = new ArrayList<>();        
        RegionApi api = googleApi.regions();
        Iterator<ListPage<Region>> listPages = api.list();
        while (listPages.hasNext()) {
            ListPage<Region> regions = listPages.next();
            for (Region region : regions) {      
                res.add(new PluginInstanceRegionModel(region.name()));
            }
        }        
        
        return res;        
    }
    private List<PluginInstanceZoneModel> getRegionZones(
            final GoogleComputeEngineApi googleApi,
            final String name){
        
        List<PluginInstanceZoneModel> res = new ArrayList<>();        
        ZoneApi api = googleApi.zones();
        Iterator<ListPage<Zone>> listPages = api.list();
        while (listPages.hasNext()) {
            ListPage<Zone> zones = listPages.next();
            for (Zone zone : zones) {     
                if(zone.name().startsWith(name)){
                    res.add(new PluginInstanceZoneModel(zone.name()));
                }
            }
        }       
        return res;            
    }
}

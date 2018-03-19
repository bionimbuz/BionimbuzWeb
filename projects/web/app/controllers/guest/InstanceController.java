package controllers.guest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.client.InstanceApi;
import app.client.RegionApi;
import app.common.Authorization;
import app.models.Body;
import app.models.PluginInstanceModel;
import app.models.PluginZoneModel;
import app.models.security.TokenModel;
import common.constants.I18N;
import common.utils.StringUtils;
import common.utils.UserCredentialsReader;
import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.ExecutorModel;
import models.ImageModel;
import models.InstanceModel;
import models.InstanceTypeModel;
import models.InstanceTypeModel.InstanceType;
import models.InstanceTypeModel.InstanceTypeZone;
import models.InstanceTypeRegionModel;
import models.PluginModel;
import models.RegionModel;
import models.RegionModel.Region;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

@For(InstanceModel.class)
@Check("/list/instances")
public class InstanceController extends BaseAdminController {

    private static final String INSTANCE_TYPE_SELECTED = "instanceTypeSelected";
    private static final String INSTANCE_TYPE_SELECTED_ID = INSTANCE_TYPE_SELECTED + ".id";
    private static final String REGION_SELECTED = "regionSelected";
    private static final String REGION_SELECTED_ID = REGION_SELECTED + ".id";

    public static void blank() throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final InstanceModel object = new InstanceModel();
        try {
            render(type, object);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/blank.html", type, object);
        }
    }
    
    public static void create(
            RegionModel regionSelected, 
            String zoneSelected,
            InstanceTypeModel instanceTypeSelected) throws Exception {
        
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final InstanceModel object = new InstanceModel();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);        
        String regionId = params.get(REGION_SELECTED_ID);
        if(regionId == null || regionId.isEmpty()) {
            regionSelected = null;
            validation.addError(REGION_SELECTED, Messages.get("validation.required"));
        }    
        String instanceTypeId = params.get(INSTANCE_TYPE_SELECTED_ID);
        if(instanceTypeId == null || instanceTypeId.isEmpty()) {
            instanceTypeSelected = null;
            validation.addError(INSTANCE_TYPE_SELECTED, Messages.get("validation.required"));
        }        
        validation.required(zoneSelected);
        if (Validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", 
                        type, object, regionSelected, zoneSelected, instanceTypeSelected);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object, regionSelected, zoneSelected, instanceTypeSelected);
            }
        }
        InstanceTypeRegionModel instanceTypeRegion = 
                InstanceTypeRegionModel.findByInstanceTypeAndRegion(instanceTypeSelected, regionSelected);
        notFoundIfNull(instanceTypeRegion);
        
        object.setPrice(instanceTypeRegion.getPrice());
        object.setPriceTableDate(instanceTypeRegion.getPriceTable().getPriceTableDate());
        object.setZoneName(zoneSelected);
        object.setRegionName(regionSelected.getName());
        object.setTypeName(instanceTypeSelected.getName());
        object.setCores(instanceTypeSelected.getCores());
        object.setMemory(instanceTypeSelected.getMemory());
        object.setCreationDate(new Date());
        
        object._save();
        if(object.isExecutionAfterCreation()) {
            executeInstance(object);            
        }
        
        flash.success(Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }
    
    private static PluginInstanceModel createPluginInstance(InstanceModel instance){
        
        PluginInstanceModel res = new PluginInstanceModel();
        
        for(ImageModel image : instance.getExecutor().getListImages()) {
            if(image.getPlugin().getId() != instance.getPlugin().getId()) {
                continue;
            }            
            res.setImageUrl(image.getUrl());
            break;
        }
        
        ExecutorModel executor = instance.getExecutor();
        res.setFirewallUdpPorts(
                StringUtils.splitToIntList(
                        executor.getFirewallUdpRules()));
        res.setFirewallTcpPorts(
                StringUtils.splitToIntList(
                        executor.getFirewallTcpRules()));        
        res.setMachineType(instance.getTypeName());
        res.setType(instance.getTypeName());
        res.setZone(instance.getZoneName());
        res.setStartupScript(instance.getExecutor().getStartupScript());
        
        return res;
    }
    
    private static void executeInstance(InstanceModel instance) {
        
        InstanceApi api = new InstanceApi(instance.getPlugin().getUrl());  
        List<PluginInstanceModel> instancesToCreate = new ArrayList();
        PluginInstanceModel instanceToCreate = 
                createPluginInstance(instance);
        instancesToCreate.add(instanceToCreate);
        
        UserCredentialsReader credentialReader = 
                new UserCredentialsReader(
                        instance.getPlugin(), 
                        instance.getCredentialUsage());
        
        try {
            for(String credential : credentialReader) {
                
                TokenModel token;
                    token = Authorization.getToken(
                            instance.getPlugin().getCloudType(),
                            instance.getPlugin().getWriteScope(),
                            credential);
    
                Body<List<PluginInstanceModel>> body = 
                        api.createInstance(
                                token.getToken(), 
                                token.getIdentity(),
                                instancesToCreate);     
                if(body.getContent() == null || body.getContent().isEmpty()) {
                    continue;
                }
                
                PluginInstanceModel instanceCreated = 
                        body.getContent().get(0);
                
                instance.setCloudInstanceName(instanceCreated.getName());
                instance.setCloudInstanceIp(instanceCreated.getExternalIp());      
                instance.save();
                break;
            }            
        } catch (Exception e) {
            e.printStackTrace();
        }         
    }
    
    public static List<Region> getRegions(final Long pluginId) {
        PluginModel plugin = PluginModel.findById(pluginId);
        if(plugin == null)
            return null;
        List<Region> listRegions = new ArrayList<>();
        for(RegionModel region : RegionModel.searchRegionsForPlugin(plugin)) {
            listRegions.add(new Region(region));
        }        
        return listRegions;
    }
    
    public static void searchRegions(final Long pluginId) {
        try {
            List<Region> listRegions = getRegions(pluginId);
            if(listRegions == null)
                notFound(Messages.get(I18N.plugin_not_found));
            renderJSON(listRegions);            
        } catch (Exception e) {
            e.printStackTrace();
            notFound(Messages.get(I18N.plugin_not_found));
        }
    }    

    private static List<String> getZones(final PluginModel plugin, final RegionModel region) {
        List<String> res = new ArrayList<>();
        RegionApi api = new RegionApi(plugin.getUrl());
        UserCredentialsReader credentialReader = 
                new UserCredentialsReader(plugin);
        try {
            for(String credential : credentialReader) {

                TokenModel token;
                    token = Authorization.getToken(
                            plugin.getCloudType(),
                            plugin.getReadScope(),
                            credential);
    
                Body<List<PluginZoneModel>> body = 
                        api.listRegionsZones(
                                token.getToken(), 
                                token.getIdentity(),
                                region.getName());     
                if(body.getContent() == null || body.getContent().isEmpty()) {
                    continue;
                }                
                for(PluginZoneModel zone : body.getContent()) {
                    res.add(zone.getName());
                }                
                return res;
            }
                  
        } catch (Exception e) {
            e.printStackTrace();
        }       
        return null;
    }
    
    private static List<InstanceType> getInstanceTypes(final RegionModel region) {
        List<InstanceType> listInstanceTypeModel = new ArrayList<>();
        for(InstanceTypeRegionModel instanceTypeRegion : 
                    InstanceTypeRegionModel.searchForRegion(region)) {
            listInstanceTypeModel.add(
                    new InstanceType(instanceTypeRegion));
        }
        return listInstanceTypeModel;
    }
    
    public static InstanceTypeZone getInstanceTypesZones(final Long pluginId, final Long regionId) {
        RegionModel region = RegionModel.findById(regionId);
        if(region == null)
            return null;
        PluginModel plugin = PluginModel.findById(pluginId);
        if(plugin == null)
            return null;
        
        List<InstanceType> listInstanceTypes = getInstanceTypes(region);
        List<String> listZones = getZones(plugin, region);
        
        return new InstanceTypeZone(listInstanceTypes, listZones);
    }
    
    public static void searchInstanceTypesZones(final Long pluginId, final Long regionId) {
        try {
            InstanceTypeZone instanceTypeZoneModel = 
                    getInstanceTypesZones(pluginId, regionId);            
            if(instanceTypeZoneModel == null)
                notFound(Messages.get(I18N.plugin_not_found));            
            renderJSON(instanceTypeZoneModel);            
        } catch (Exception e) {
            e.printStackTrace();
            notFound(Messages.get(I18N.plugin_not_found));
        }
    }    
}
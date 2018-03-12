package controllers.guest;

import java.util.ArrayList;
import java.util.List;

import common.constants.I18N;
import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.InstanceModel;
import models.InstanceTypeModel;
import models.InstanceTypeModel.InstanceType;
import models.InstanceTypeZoneModel;
import models.PluginModel;
import models.ZoneModel;
import models.ZoneModel.Zone;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

@For(InstanceModel.class)
@Check("/list/instances")
public class InstanceController extends BaseAdminController {

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
            ZoneModel zoneSelected, 
            InstanceTypeModel instanceTypeSelected) throws Exception {
        
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final InstanceModel object = new InstanceModel();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);        
        String zoneId = params.get("zoneSelected.id");
        if(zoneId == null || zoneId.isEmpty()) {
            zoneSelected = null;
            validation.addError("zoneSelected", Messages.get("validation.required"));
        }
        String instanceTypeId = params.get("instanceTypeSelected.id");
        if(instanceTypeId == null || instanceTypeId.isEmpty()) {
            instanceTypeSelected = null;
            validation.addError("instanceTypeSelected", Messages.get("validation.required"));
        }        
        if (Validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", 
                        type, object, zoneSelected, instanceTypeSelected);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object, zoneSelected, instanceTypeSelected);
            }
        }
        InstanceTypeZoneModel instanceTypeZone = 
                InstanceTypeZoneModel.findByInstanceTypeAndZone(instanceTypeSelected, zoneSelected);
        notFoundIfNull(instanceTypeZone);
        
        object.setPrice(instanceTypeZone.getPrice());
        object.setPriceTableDate(instanceTypeZone.getPriceTable().getPriceTableDate());
        object.setZoneName(zoneSelected.getName());
        object.setTypeName(instanceTypeSelected.getName());
        object.setCores(instanceTypeSelected.getCores());
        object.setMemory(instanceTypeSelected.getMemory());
        
        object._save();
        flash.success(Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }
    
    public static List<Zone> getZones(final Long pluginId) {
        PluginModel plugin = PluginModel.findById(pluginId);
        if(plugin == null)
            return null;
        List<Zone> listZones = new ArrayList<>();
        for(ZoneModel zone : ZoneModel.searchZonesForPlugin(plugin)) {
            listZones.add(new Zone(zone));
        }        
        return listZones;
    }
    
    public static void searchZones(final Long pluginId) {
        try {
            List<Zone> listZones = getZones(pluginId);
            if(listZones == null)
                notFound(Messages.get(I18N.plugin_not_found));
            renderJSON(listZones);            
        } catch (Exception e) {
            e.printStackTrace();
            notFound(Messages.get(I18N.plugin_not_found));
        }
    } 
    
    public static List<InstanceType> getInstanceTypes(final Long zoneId) {
        ZoneModel zone = ZoneModel.findById(zoneId);
        if(zone == null)
            return null;
        List<InstanceType> listInstanceTypeModel = new ArrayList<>();
        for(InstanceTypeZoneModel instanceTypeZone : 
                    InstanceTypeZoneModel.searchForZone(zone)) {
            listInstanceTypeModel.add(
                    new InstanceType(instanceTypeZone));
        }
        return listInstanceTypeModel;
    }
    
    public static void searchInstanceTypes(final Long zoneId) {
        try {
            List<InstanceType> listInstanceTypeModel = getInstanceTypes(zoneId);
            if(listInstanceTypeModel == null)
                notFound(Messages.get(I18N.plugin_not_found));            
            renderJSON(listInstanceTypeModel);            
        } catch (Exception e) {
            e.printStackTrace();
            notFound(Messages.get(I18N.plugin_not_found));
        }
    }    
}
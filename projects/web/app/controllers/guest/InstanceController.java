package controllers.guest;

import java.util.ArrayList;
import java.util.List;

import common.constants.I18N;
import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.InstanceModel;
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
    
    public static void create(PluginModel pluginSelected, ZoneModel zoneSelected) throws Exception {
        String pluginId = params.get("pluginSelected.id");
        if(pluginId == null || pluginId.isEmpty()) {
            pluginSelected = null;
        }        
        String zoneId = params.get("zoneSelected.id");
        if(zoneId == null || zoneId.isEmpty()) {
            zoneSelected = null;
        }
        
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final InstanceModel object = new InstanceModel();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        validation.required(pluginSelected);
        validation.required(zoneSelected);
        if (Validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", 
                        type, object, pluginSelected, zoneSelected);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object, pluginSelected, zoneSelected);
            }
        }
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
    
    public static void searchZones(final Long pluginId) {
        try {
            PluginModel plugin = PluginModel.findById(pluginId);
            if(plugin == null)
                notFound(Messages.get(I18N.plugin_not_found));

            List<Zone> listZones = new ArrayList<>();
            for(ZoneModel zone : ZoneModel.searchZonesForPlugin(plugin)) {
                listZones.add(new Zone(zone));
            }
            renderJSON(listZones);            
        } catch (Exception e) {
            e.printStackTrace();
            notFound(Messages.get(I18N.plugin_not_found));
        }
    } 
    
    public static void searchInstanceTypes(final Long zoneId) {
        try {
            ZoneModel zone = ZoneModel.findById(zoneId);
            if(zone == null)
                notFound(Messages.get(I18N.plugin_not_found));

            List<InstanceType> listInstanceTypeModel = new ArrayList<>();
            for(InstanceTypeZoneModel instanceTypeZone : 
                        InstanceTypeZoneModel.searchForZone(zone)) {
                listInstanceTypeModel.add(
                        new InstanceType(instanceTypeZone));
            }
            
            renderJSON(listInstanceTypeModel);            
        } catch (Exception e) {
            e.printStackTrace();
            notFound(Messages.get(I18N.plugin_not_found));
        }
    }    
}
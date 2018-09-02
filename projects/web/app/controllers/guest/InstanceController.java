package controllers.guest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.client.ComputingApi;
import app.common.Authorization;
import app.common.utils.StringUtils;
import app.models.Body;
import app.models.PluginComputingZoneModel;
import app.models.security.TokenModel;
import common.constants.I18N;
import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import jobs.InstanceCreationJob;
import models.ApplicationArgumentsModel;
import models.ApplicationFileInputModel;
import models.ApplicationFileOutputModel;
import models.ApplicationFileOutputModel.ApplicationOutput;
import models.ExecutorModel;
import models.InstanceModel;
import models.InstanceModel.CredentialUsagePolicy;
import models.InstanceTypeModel;
import models.InstanceTypeModel.InstanceType;
import models.InstanceTypeModel.InstanceTypeZone;
import models.InstanceTypeRegionModel;
import models.PluginModel;
import models.RegionModel;
import models.RegionModel.Region;
import models.SpaceFileModel;
import models.SpaceModel;
import models.VwCredentialModel;
import models.VwSpaceModel;
import play.Logger;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

@For(InstanceModel.class)
@Check("/list/instances")
public class InstanceController extends BaseAdminController {

    private static final String EXECUTOR_SELECTED = "executorSelected";
    private static final String EXECUTOR_SELECTED_ID = "executorSelected.id";
    private static final String INSTANCE_TYPE_SELECTED = "instanceTypeSelected";
    private static final String INSTANCE_TYPE_SELECTED_ID = INSTANCE_TYPE_SELECTED + ".id";
    private static final String REGION_SELECTED = "regionSelected";
    private static final String REGION_SELECTED_ID = REGION_SELECTED + ".id";

    public static void blank(ExecutorModel executorSelected) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        String executorId = params.get(EXECUTOR_SELECTED_ID);
        if(StringUtils.isEmpty(executorId) ){
            executorSelected = null;
        }        
        final InstanceModel object = new InstanceModel();
        object.setExecutor(executorSelected);        
        List<VwSpaceModel> listSpaces = 
                VwSpaceModel.searchForCurrentUserWithShared();        
        try {
            render(type, object, executorSelected, listSpaces);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/blank.html", type, object, executorSelected, listSpaces);
        }
    }
    
    private static ApplicationArgumentsModel bindApplicationArguments() {
        
        ApplicationArgumentsModel res = new ApplicationArgumentsModel();     

        // Process option arguments
        final String applicationArguments = "";
        Binder.bindBean(params.getRootParamNode(), "applicationArguments", applicationArguments);   
        res.setArguments(applicationArguments);
        
        // Process input files
        Integer [] applicationInputFiles = new Integer [] {};
        Binder.bindBean(params.getRootParamNode(), "applicationInputFiles", applicationInputFiles);   
        
        List<ApplicationFileInputModel> inputFiles = new ArrayList();
        for(int i = 0; i<applicationInputFiles.length; i++) {
            Integer spaceFileId = applicationInputFiles[i];
            if(spaceFileId == null)
                continue;            
            SpaceFileModel spaceFile = 
                    SpaceFileModel.findById(spaceFileId);            
            ApplicationFileInputModel inputFile = 
                    new ApplicationFileInputModel();            
            inputFile.setOrder(i);
            inputFile.setSpaceFile(spaceFile);
            inputFiles.add(inputFile);            
        }
        res.setApplicationInputFiles(inputFiles);
        
        // Process output files
        ApplicationOutput [] applicationOutputFiles = new ApplicationOutput [] {};
        Binder.bindBean(params.getRootParamNode(), "applicationOutputFiles", applicationOutputFiles);   

        List<ApplicationFileOutputModel> outputFiles = new ArrayList(); 
        for(int i = 0; i<applicationOutputFiles.length; i++) {
            ApplicationOutput spaceOutputFile = applicationOutputFiles[i];
            if(spaceOutputFile == null 
                || spaceOutputFile.getSpaceId() == null
                || StringUtils.isEmpty(spaceOutputFile.getName())) {
                continue;            
            }
            SpaceModel space = 
                    SpaceModel.findById(spaceOutputFile.getSpaceId());         
            
            SpaceFileModel spaceFile = new SpaceFileModel();
            spaceFile.setName(spaceOutputFile.getName());
            spaceFile.setSpace(space);
            spaceFile.setVirtualName(SpaceFileModel.generateVirtualName(spaceOutputFile.getName()));
            
            ApplicationFileOutputModel outputFile = 
                    new ApplicationFileOutputModel();            
            outputFile.setOrder(i);
            outputFile.setSpaceFile(spaceFile);
            outputFiles.add(outputFile);            
        }
        res.setApplicationOutputFiles(outputFiles);
        
        return res;
    }

    public static void create(
            RegionModel regionSelected,
            String zoneSelected,
            InstanceTypeModel instanceTypeSelected) throws Exception {

        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);        
        ApplicationArgumentsModel arguments = bindApplicationArguments();
        final InstanceModel object = new InstanceModel();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);        
        String regionId = params.get(REGION_SELECTED_ID);
        if(StringUtils.isEmpty(regionId)) {
            regionSelected = null;
            validation.addError(REGION_SELECTED, Messages.get("validation.required"));
        }
        String instanceTypeId = params.get(INSTANCE_TYPE_SELECTED_ID);
        if(StringUtils.isEmpty(instanceTypeId)) {
            instanceTypeSelected = null;
            validation.addError(INSTANCE_TYPE_SELECTED, Messages.get("validation.required"));
        }
        validation.required(zoneSelected);
        
        if (Validation.hasErrors()) {
            List<VwSpaceModel> listSpaces = 
                    VwSpaceModel.searchForCurrentUserWithShared(); 
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html",
                        type, object, regionSelected, zoneSelected, instanceTypeSelected, listSpaces);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object, regionSelected, zoneSelected, instanceTypeSelected, listSpaces);
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
            InstanceCreationJob.create(object);
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

    public static void delete(final Long id) throws Exception {
        
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final InstanceModel object = InstanceModel.findById(id);
        notFoundIfNull(object);
        
        try {
            ComputingApi api = new ComputingApi(object.getPlugin().getUrl());
            String credentialData = 
                    object.getCredential().getCredentialData().getContentAsString();
            TokenModel token;
                token = Authorization.getToken(
                        object.getPlugin().getCloudType(),
                        object.getPlugin().getInstanceWriteScope(),
                        credentialData);

            Body<Boolean> body =
                    api.deleteInstance(
                            token.getToken(),
                            token.getIdentity(),
                            object.getRegionName(),
                            object.getZoneName(),
                            object.getCloudInstanceName());
            
            if(body != null 
                    && body.getContent() != null 
                    && body.getContent()) {
                object.delete();
                flash.success(Messages.get("crud.deleted", type.modelName));
                redirect(request.controller + ".list");
            }        
        } catch (final Exception e) {
            Logger.warn(e, "Error deleting instance [%s]", e.getMessage());
        }
        
        flash.error(Messages.get("crud.delete.error", type.modelName));
        redirect(request.controller + ".show", object._key());
    }

    public static List<Region> getInstanceRegions(final Long pluginId) {
        PluginModel plugin = PluginModel.findById(pluginId);
        if(plugin == null)
            return null;
        List<Region> listRegions = new ArrayList<>();
        for(RegionModel region : RegionModel.searchInstanceRegionsForPlugin(plugin)) {
            listRegions.add(new Region(region));
        }
        return listRegions;
    }

    public static void searchRegions(final Long pluginId) {
        try {
            List<Region> listRegions = getInstanceRegions(pluginId);
            if(listRegions == null)
                notFound(Messages.get(I18N.not_found));
            renderJSON(listRegions);
        } catch (Exception e) {
            Logger.error(e, "Error searching regions [%s]", e.getMessage());
            notFound(Messages.get(I18N.not_found));
        }
    }

    private static List<String> getZones(final PluginModel plugin, final RegionModel region) {
        
        try {
            
            List<String> res = new ArrayList<>();
            ComputingApi api = new ComputingApi(plugin.getUrl());
            
            List<VwCredentialModel> listCredentials = 
                    VwCredentialModel.searchForCurrentUserAndPlugin(
                            plugin.getId(),
                            CredentialUsagePolicy.OWNER_FIRST);   

            for(VwCredentialModel vwCredential : listCredentials) {

                try {
                    String credentialData = vwCredential
                            .getCredentialData()
                            .getContentAsString();
                    
                    TokenModel token;
                        token = Authorization.getToken(
                                plugin.getCloudType(),
                                plugin.getInstanceReadScope(),
                                credentialData);
    
                    Body<List<PluginComputingZoneModel>> body =
                            api.listRegionZones(
                                    token.getToken(),
                                    token.getIdentity(),
                                    region.getName());
                    
                    if(body == null || body.getContent() == null || body.getContent().isEmpty()) {
                        continue;
                    }
                    for(PluginComputingZoneModel zone : body.getContent()) {
                        res.add(zone.getName());
                    }
                    return res;
                } catch (Exception e) {
                    Logger.warn(e, "Operation cannot be completed with credential [%s]", e.getMessage());
                }
            }
        } catch (Exception e) {
            Logger.error(e, "Error searching zones [%s]", e.getMessage());
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
                notFound(Messages.get(I18N.not_found));
            renderJSON(instanceTypeZoneModel);
        } catch (Exception e) {
            Logger.error(e, "Error searching instance type zones [%s]", e.getMessage());
            notFound(Messages.get(I18N.not_found));
        }
    }
}
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

    private static final String EXECUTOR_SELECTED_ID = "executorSelected.id";
    private static final String INSTANCE_TYPE_SELECTED = "instanceTypeSelected";
    private static final String INSTANCE_TYPE_SELECTED_ID = INSTANCE_TYPE_SELECTED + ".id";
    private static final String REGION_SELECTED = "regionSelected";
    private static final String REGION_SELECTED_ID = REGION_SELECTED + ".id";

    public static void blank(
            ExecutorModel executorSelected) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final String executorId = params.get(EXECUTOR_SELECTED_ID);
        if (StringUtils.isEmpty(executorId)) {
            executorSelected = null;
        }
        final InstanceModel object = new InstanceModel();
        object.setExecutor(executorSelected);
        final List<VwSpaceModel> listSpaces = VwSpaceModel.searchForCurrentUserWithShared();
        try {
            render(type, object, executorSelected, listSpaces);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/blank.html", type, object, executorSelected, listSpaces);
        }
    }

    public static void create(
            RegionModel regionSelected,
            final String zoneSelected,
            InstanceTypeModel instanceTypeSelected,
            final String applicationArguments,
            final List<Long> applicationInputFiles,
            final List<Long> applicationOutputFileSpaces,
            final List<String> applicationOutputFileNames) throws Exception {

        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final ApplicationArgumentsModel arguments = bindApplicationArguments(
                applicationArguments,
                applicationInputFiles,
                applicationOutputFileSpaces,
                applicationOutputFileNames);
        final InstanceModel object = new InstanceModel();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        ExecutorModel executorSelected = object.getExecutor();
        validation.valid(object);
        final String regionId = params.get(REGION_SELECTED_ID);
        if (StringUtils.isEmpty(regionId)) {
            regionSelected = null;
            validation.addError(REGION_SELECTED, Messages.get("validation.required"));
        }
        final String instanceTypeId = params.get(INSTANCE_TYPE_SELECTED_ID);
        if (StringUtils.isEmpty(instanceTypeId)) {
            instanceTypeSelected = null;
            validation.addError(INSTANCE_TYPE_SELECTED, Messages.get("validation.required"));
        }
        validation.required(zoneSelected);

        if (Validation.hasErrors()) {
            final List<VwSpaceModel> listSpaces = VwSpaceModel.searchForCurrentUserWithShared();
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html",
                        type, object, executorSelected, regionSelected, zoneSelected, instanceTypeSelected, listSpaces);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object, executorSelected, regionSelected, zoneSelected, instanceTypeSelected, listSpaces);
            }
        }
        final InstanceTypeRegionModel instanceTypeRegion = InstanceTypeRegionModel.findByInstanceTypeAndRegion(instanceTypeSelected, regionSelected);
        notFoundIfNull(instanceTypeRegion);

        object.setPrice(instanceTypeRegion.getPrice());
        object.setPriceTableDate(instanceTypeRegion.getPriceTable().getPriceTableDate());
        object.setZoneName(zoneSelected);
        object.setRegionName(regionSelected.getName());
        object.setTypeName(instanceTypeSelected.getName());
        object.setCores(instanceTypeSelected.getCores());
        object.setMemory(instanceTypeSelected.getMemory());
        object.setCreationDate(new Date());
        object.setApplicationArguments(arguments);

        object._save();
        if (object.isExecutionAfterCreation()) {
            InstanceCreationJob.create(object, getConnectedUser().getId());
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
            final ComputingApi api = new ComputingApi(object.getPlugin().getUrl());
            final String credentialData = object.getCredential().getCredentialData().getContentAsString();
            TokenModel token;
            token = Authorization.getToken(
                    object.getPlugin().getCloudType(),
                    object.getPlugin().getInstanceWriteScope(),
                    credentialData);

            final Body<Boolean> body = api.deleteInstance(
                    token.getToken(),
                    token.getIdentity(),
                    object.getRegionName(),
                    object.getZoneName(),
                    object.getCloudInstanceName());

            if (body != null
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
        final PluginModel plugin = PluginModel.findById(pluginId);
        if (plugin == null) {
            return null;
        }
        final List<Region> listRegions = new ArrayList<>();
        for (final RegionModel region : RegionModel.searchInstanceRegionsForPlugin(plugin)) {
            listRegions.add(new Region(region));
        }
        return listRegions;
    }

    public static void searchRegions(final Long pluginId) {
        try {
            final List<Region> listRegions = getInstanceRegions(pluginId);
            if (listRegions == null) {
                notFound(Messages.get(I18N.not_found));
            }
            renderJSON(listRegions);
        } catch (final Exception e) {
            Logger.error(e, "Error searching regions [%s]", e.getMessage());
            notFound(Messages.get(I18N.not_found));
        }
    }

    private static ApplicationArgumentsModel bindApplicationArguments(
            final String applicationArguments,
            final List<Long> applicationInputFiles,
            final List<Long> applicationOutputFileSpaces,
            final List<String> applicationOutputFileNames) {

        final ApplicationArgumentsModel object = new ApplicationArgumentsModel();

        // Process option arguments
        object.setArguments(applicationArguments);

        // Process input files
        for (int i = 0; i < applicationInputFiles.size(); i++) {
            final Long spaceFileId = applicationInputFiles.get(i);
            if (spaceFileId == null) {
                continue;
            }
            final SpaceFileModel spaceFile = SpaceFileModel.findById(spaceFileId);
            final ApplicationFileInputModel inputFile = new ApplicationFileInputModel();
            inputFile.setFileOrder(i);
            inputFile.setSpaceFile(spaceFile);
            object.addInputFile(inputFile);
        }

        // Process output files
        for (int i = 0; i < applicationOutputFileSpaces.size(); i++) {
            final Long spaceId = applicationOutputFileSpaces.get(i);
            if (spaceId == null) {
                continue;
            }
            final SpaceModel space = SpaceModel.findById(spaceId);
            if (i >= applicationOutputFileNames.size()) {
                break;
            }

            final String fileName = applicationOutputFileNames.get(i);
            if (StringUtils.isEmpty(fileName)) {
                break;
            }

            final SpaceFileModel spaceFile = new SpaceFileModel();
            spaceFile.setName(fileName);
            spaceFile.setSpace(space);
            spaceFile.setVirtualName(
                    SpaceFileModel.generateVirtualName(fileName));

            final ApplicationFileOutputModel outputFile = new ApplicationFileOutputModel();
            outputFile.setFileOrder(i);
            outputFile.setSpaceFile(spaceFile);
            object.addOutputFile(outputFile);
        }

        return object;
    }

    private static List<String> getZones(final PluginModel plugin, final RegionModel region) {

        try {

            final List<String> res = new ArrayList<>();
            final ComputingApi api = new ComputingApi(plugin.getUrl());

            final List<VwCredentialModel> listCredentials = VwCredentialModel.searchForCurrentUserAndPlugin(
                    plugin.getId(),
                    CredentialUsagePolicy.OWNER_FIRST);

            for (final VwCredentialModel vwCredential : listCredentials) {

                try {
                    final String credentialData = vwCredential
                            .getCredentialData()
                            .getContentAsString();

                    TokenModel token;
                    token = Authorization.getToken(
                            plugin.getCloudType(),
                            plugin.getInstanceReadScope(),
                            credentialData);

                    final Body<List<PluginComputingZoneModel>> body = api.listRegionZones(
                            token.getToken(),
                            token.getIdentity(),
                            region.getName());

                    if (body == null || body.getContent() == null || body.getContent().isEmpty()) {
                        continue;
                    }
                    for (final PluginComputingZoneModel zone : body.getContent()) {
                        res.add(zone.getName());
                    }
                    return res;
                } catch (final Exception e) {
                    Logger.warn(e, "Operation cannot be completed with credential [%s]", e.getMessage());
                }
            }
        } catch (final Exception e) {
            Logger.error(e, "Error searching zones [%s]", e.getMessage());
        }
        return null;
    }

    private static List<InstanceType> getInstanceTypes(final RegionModel region) {
        final List<InstanceType> listInstanceTypeModel = new ArrayList<>();
        for (final InstanceTypeRegionModel instanceTypeRegion : InstanceTypeRegionModel.searchForRegion(region)) {
            listInstanceTypeModel.add(
                    new InstanceType(instanceTypeRegion));
        }
        return listInstanceTypeModel;
    }

    public static InstanceTypeZone getInstanceTypesZones(final Long pluginId, final Long regionId) {
        final RegionModel region = RegionModel.findById(regionId);
        if (region == null) {
            return null;
        }
        final PluginModel plugin = PluginModel.findById(pluginId);
        if (plugin == null) {
            return null;
        }

        final List<InstanceType> listInstanceTypes = getInstanceTypes(region);
        final List<String> listZones = getZones(plugin, region);

        return new InstanceTypeZone(listInstanceTypes, listZones);
    }

    public static void searchInstanceTypesZones(final Long pluginId, final Long regionId) {
        try {
            final InstanceTypeZone instanceTypeZoneModel = getInstanceTypesZones(pluginId, regionId);
            if (instanceTypeZoneModel == null) {
                notFound(Messages.get(I18N.not_found));
            }
            renderJSON(instanceTypeZoneModel);
        } catch (final Exception e) {
            Logger.error(e, "Error searching instance type zones [%s]", e.getMessage());
            notFound(Messages.get(I18N.not_found));
        }
    }
}
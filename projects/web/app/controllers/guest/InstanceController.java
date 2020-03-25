package controllers.guest;

//import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.client.ComputingApi;
import app.common.Authorization;
import app.common.utils.StringUtils;
import app.models.Body;
import app.models.ExecutionStatus.EXECUTION_PHASE;
import app.models.ExecutionStatus.STATUS;
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
import models.WorkflowModel.WORKFLOW_STATUS;
import models.WorkflowNodeModel;
import play.Logger;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

@For(InstanceModel.class)
@Check("/list/instances")
public class InstanceController extends BaseAdminController {

    private static final String CONTROLLERS_GUEST_WORKFLOW_CONTROLLER_SHOW = "controllers.guest.WorkflowController.show";
    private static final String CONTROLLERS_GUEST_INSTANCE_CONTROLLER_BLANK = "controllers.guest.InstanceController.blank";
    private static final String CONTROLLERS_GUEST_INSTANCE_CONTROLLER_SHOW = "controllers.guest.InstanceController.show";
    private static final String EXECUTOR_SELECTED_ID = "executorSelected.id";
    private static final String INSTANCE_TYPE_SELECTED = "instanceTypeSelected";
    private static final String INSTANCE_TYPE_SELECTED_ID = INSTANCE_TYPE_SELECTED + ".id";
    private static final String REGION_SELECTED = "regionSelected";
    private static final String REGION_SELECTED_ID = REGION_SELECTED + ".id";

    public static void copy(final Long id) throws Exception {
    	
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final InstanceModel objectFound = InstanceModel.findById(id);
        notFoundIfNull(objectFound);

        final InstanceModel object = new InstanceModel();
        object.setApplicationArguments(objectFound.getApplicationArguments());
        object.setCredentialUsage(objectFound.getCredentialUsage());
        //object.setExecutionAfterCreation(objectFound.isExecutionAfterCreation());
        object.setExecutionAfterCreation(false); // TODO: Erro ao criar uma instância no Workflow, comentando para teste (Augusto e Fabiana)
        object.setExecutor(objectFound.getExecutor());
        object.setPlugin(objectFound.getPlugin());
        object.setRegionName(objectFound.getRegionName());
        object.setTypeName(objectFound.getTypeName());
        object.setZoneName(objectFound.getZoneName());

        final ExecutorModel executorSelected = objectFound.getExecutor();
        final String zoneSelected = objectFound.getZoneName();
        final String applicationArguments = objectFound.getApplicationArguments().getArguments();

        final List<Long> applicationInputFileSpaces = new ArrayList<>();
        final List<Long> applicationInputFiles = new ArrayList<>();
        for(final ApplicationFileInputModel inputFile : objectFound.getApplicationArguments().getApplicationInputFiles()) {
            applicationInputFileSpaces.add(inputFile.getSpaceFile().getSpace().getId());
            applicationInputFiles.add(inputFile.getSpaceFile().getId());
        }
        final List<Long> applicationOutputFileSpaces = new ArrayList<>();
        final List<String> applicationOutputFileNames = new ArrayList<>();
        for(final ApplicationFileOutputModel outputFile : objectFound.getApplicationArguments().getApplicationOutputFiles()) {
            applicationOutputFileSpaces.add(outputFile.getSpaceFile().getSpace().getId());
            applicationOutputFileNames.add(outputFile.getSpaceFile().getName());
        }
        final List<VwSpaceModel> listSpaces = VwSpaceModel.searchForCurrentUserWithShared();

        render("guest/InstanceController/blank.html",
                type,
                object,
                executorSelected,
                zoneSelected, listSpaces, applicationArguments,
                applicationInputFileSpaces, applicationInputFiles,
                applicationOutputFileSpaces, applicationOutputFileNames);
    }

    public static void blank(
            final Long workflowNodeId,
            ExecutorModel executorSelected) throws Exception {

        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        WorkflowNodeModel workflowNode = null;
        if (workflowNodeId != null) {
            workflowNode = WorkflowNodeModel.findById(workflowNodeId);
        }
        final String executorId = params.get(EXECUTOR_SELECTED_ID);
        if (StringUtils.isEmpty(executorId)) {
            executorSelected = null;
        }
        final InstanceModel object = new InstanceModel();
        object.setWorkflowNode(workflowNode);
        object.setExecutor(executorSelected);
        final List<VwSpaceModel> listSpaces = VwSpaceModel.searchForCurrentUserWithShared();
        try {
            render(type, object, workflowNodeId, executorSelected, listSpaces);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/blank.html", type, object, workflowNodeId, executorSelected, listSpaces);
        }
    }

    public static void create(
            final Long workflowNodeId,
            RegionModel regionSelected,
            final String zoneSelected,
            InstanceTypeModel instanceTypeSelected,
            final String applicationArguments,
            final List<Long> applicationInputFileSpaces,
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
        if (object.getWorkflowNode() != null && object.getWorkflowNode().getId() == null) {
            object.setWorkflowNode(null);
        }
        final ExecutorModel executorSelected = object.getExecutor();
        WorkflowNodeModel workflowNode = null;
        if (workflowNodeId != null) {
            workflowNode = WorkflowNodeModel.findById(workflowNodeId);
            notFoundIfNull(workflowNode);
            object.setWorkflowNode(workflowNode);
        }
        validation.valid(object);
        final String regionId = params.get(REGION_SELECTED_ID);
        if (StringUtils.isEmpty(regionId)) {
            regionSelected = null;
            Validation.addError(REGION_SELECTED, Messages.get("validation.required"));
        }
        final String instanceTypeId = params.get(INSTANCE_TYPE_SELECTED_ID);
        if (StringUtils.isEmpty(instanceTypeId)) {
            instanceTypeSelected = null;
            Validation.addError(INSTANCE_TYPE_SELECTED, Messages.get("validation.required"));
        }
        validation.required(zoneSelected);

        if (Validation.hasErrors()) {
            final List<VwSpaceModel> listSpaces = VwSpaceModel.searchForCurrentUserWithShared();
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html",
                        type, object, executorSelected, workflowNodeId,
                        regionSelected, zoneSelected, instanceTypeSelected,
                        listSpaces, applicationArguments,
                        applicationInputFileSpaces, applicationInputFiles,
                        applicationOutputFileSpaces, applicationOutputFileNames);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/blank.html",
                        type, object, workflowNodeId, executorSelected,
                        regionSelected, zoneSelected, instanceTypeSelected,
                        listSpaces, applicationArguments,
                        applicationInputFileSpaces, applicationInputFiles,
                        applicationOutputFileSpaces, applicationOutputFileNames);
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

        if (workflowNode != null) {
            workflowNode.setInstance(object);
            workflowNode.save();
        }
        // TODO: Erro ao criar uma instância no Workflow, comentando para teste (Augusto e Fabiana)
        object.setExecutionAfterCreation(false);
        if (object.isExecutionAfterCreation()) {
            new InstanceCreationJob(object.getId(), getConnectedUser().getId()).now();
        } else {
            object.setStatus(STATUS.WAITING);
            object.setPhase(EXECUTION_PHASE.WAITING);
            object.save();
        }

        flash.success(Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            if (workflowNodeId != null) {
                final Long id = workflowNode.getWorkflow().getId();
                redirect(CONTROLLERS_GUEST_WORKFLOW_CONTROLLER_SHOW, id);
            } else {
                redirect(request.controller + ".list");
            }
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }

    public static void delete(Long id) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final InstanceModel object = InstanceModel.findById(id);
        notFoundIfNull(object);
        try {
            if (object.getWorkflowNode() != null) {
                if (object.getWorkflowNode().getWorkflow().getStatus() == WORKFLOW_STATUS.EDITING) {
                    object.delete();
                    id = object.getWorkflowNode().getWorkflow().getId();
                    redirect(CONTROLLERS_GUEST_WORKFLOW_CONTROLLER_SHOW, id);
                }
            } else {
                final ComputingApi api = new ComputingApi(object.getPlugin().getUrl());
                final String credentialData = object.getCredential().getCredentialData().getContentAsString();
                final TokenModel token = Authorization.getToken(
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
        if (applicationInputFiles != null) {
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
        }

        // Process output files
        if (applicationOutputFileSpaces != null) {
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

    public static void createNode(final Long id) {
        final WorkflowNodeModel workflowNode = WorkflowNodeModel.findById(id);
        notFoundIfNull(workflowNode);
        final Long workflowNodeId = id;
        if (workflowNode.getInstance() != null) {
            redirect(CONTROLLERS_GUEST_INSTANCE_CONTROLLER_SHOW,
                    workflowNode.getInstance().getId());
        } else {
            redirect(CONTROLLERS_GUEST_INSTANCE_CONTROLLER_BLANK,
                    workflowNodeId);
        }
    }

    public static void executeInstance(final Long id) {

        final InstanceModel object = InstanceModel.findById(id);
        // TODO: Erro ao criar uma instância no Workflow, comentando para teste (Augusto e Fabiana)
        // if (!object.isExecutionAfterCreation()) {
            object.setExecutionAfterCreation(true);
            object.setStatus(STATUS.IDDLE);
            object.setPhase(EXECUTION_PHASE.WAITING);
            //object.setExecutionStart(new Date());
            //object.setExecutionEnd(null);
            
            object.save();
            new InstanceCreationJob(object.getId(), getConnectedUser().getId()).now();
        //}
        redirect(request.controller + ".list");
    }

}

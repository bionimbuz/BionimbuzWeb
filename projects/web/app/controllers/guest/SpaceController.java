package controllers.guest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.client.StorageApi;
import app.common.Authorization;
import app.models.Body;
import app.models.PluginStorageModel;
import app.models.security.TokenModel;
import common.constants.I18N;
import common.utils.UserCredentialsReader;
import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.InstanceModel.CredentialUsagePolicy;
import models.PluginModel;
import models.SpaceModel;
import models.StorageRegionModel;
import models.StorageRegionModel.StorageRegion;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

@For(SpaceModel.class)
@Check("/list/spaces")
public class SpaceController extends BaseAdminController {

    private static final String STORAGE_REGION_SELECTED = "storageRegionSelected";
    private static final String OBJECT_NAME = "object.name";
    private static final String STORAGE_REGION_SELECTED_ID = STORAGE_REGION_SELECTED + ".id";

    public static void create(
            StorageRegionModel storageRegionSelected) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);

        final SpaceModel object = new SpaceModel();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        String regionId = params.get(STORAGE_REGION_SELECTED_ID);
        if(regionId == null || regionId.isEmpty()) {
            storageRegionSelected = null;
            validation.addError(STORAGE_REGION_SELECTED, Messages.get("validation.required"));
        }
        if(!Validation.hasErrors()) {
            object.setRegionName(storageRegionSelected.getRegion().getName());
            object.setPricePerGB(storageRegionSelected.getPrice());
            object.setClassAPrice(storageRegionSelected.getClassAPrice());
            object.setClassBPrice(storageRegionSelected.getClassBPrice());
            object.setPriceTableDate(storageRegionSelected.getPriceTable().getPriceTableDate());
            object.setCreationDate(new Date());

            boolean hasError = false;
            if(object.isAlocationAfterCreation()) {
                hasError = createSpace(object);
            }
            if(!hasError) {
                object._save();
            } else {
                validation.addError(OBJECT_NAME, Messages.get("error.space.not.created"));
            }
        }
        if (Validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object, storageRegionSelected);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object, storageRegionSelected);
            }
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

    public static void searchStorageRegions(final Long pluginId) {
        try {
            List<StorageRegion> listStorageRegions =
                    getStorageRegions(pluginId);
            if(listStorageRegions == null)
                notFound(Messages.get(I18N.plugin_not_found));
            renderJSON(listStorageRegions);
        } catch (Exception e) {
            e.printStackTrace();
            notFound(Messages.get(I18N.plugin_not_found));
        }
    }

    private static List<StorageRegion> getStorageRegions(final Long pluginId) {
        PluginModel plugin = PluginModel.findById(pluginId);
        if(plugin == null)
            return null;
        List<StorageRegionModel> listStorageRegionsFound =
                StorageRegionModel.searchByPlugin(pluginId);
        List<StorageRegion> listStorageRegions = new ArrayList<>();

        for(StorageRegionModel storageRegion : listStorageRegionsFound) {
            listStorageRegions.add(
                    new StorageRegion(
                            storageRegion.getId(),
                            storageRegion.getRegion().getName(),
                            storageRegion.getPrice(),
                            storageRegion.getClassAPrice(),
                            storageRegion.getClassBPrice()));
        }
        return listStorageRegions;
    }


    private static boolean createSpace(SpaceModel space) {

        PluginModel plugin = space.getPlugin();
        StorageApi api = new StorageApi(plugin.getUrl());
        PluginStorageModel spaceToCreate =
                new PluginStorageModel(space.getName(), space.getRegionName());

        UserCredentialsReader credentialReader =
                new UserCredentialsReader(
                        plugin,
                        CredentialUsagePolicy.ONLY_OWNER);
        for(String credential : credentialReader) {
            try {
                TokenModel token;
                    token = Authorization.getToken(
                            plugin.getCloudType(),
                            plugin.getStorageWriteScope(),
                            credential);

                Body<PluginStorageModel> body =
                        api.createSpace(
                                token.getToken(),
                                token.getIdentity(),
                                spaceToCreate);
                if(body == null || body.getContent() == null) {
                    continue;
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
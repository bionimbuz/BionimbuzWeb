package controllers.guest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.client.StorageApi;
import app.common.Authorization;
import app.models.Body;
import app.models.PluginInfoModel.AuthenticationType;
import app.models.PluginStorageFileDownloadModel;
import app.models.PluginStorageFileUploadModel;
import app.models.RemoteFileInfo;
import app.models.security.TokenModel;
import common.constants.I18N;
import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.CredentialModel;
import models.PluginModel;
import models.SpaceFileModel;
import models.SpaceFileModel.SpaceFile;
import models.SpaceModel;
import play.Logger;
import play.i18n.Messages;

@For(SpaceFileModel.class)
@Check("/list/space/files")
public class SpaceFileController extends BaseAdminController {
    
    public static void searchFilesForSpace(final Long spaceId) {
        try {
            List<SpaceFile> listSpaceFiles = getSpaceFiles(spaceId);
            if(listSpaceFiles == null)
                notFound(Messages.get(I18N.not_found));
            renderJSON(listSpaceFiles);
        } catch (Exception e) {
            Logger.error(e, "Error searching files from space [%s]", e.getMessage());
            notFound(Messages.get(I18N.not_found));
        }
    }
    
    public static List<SpaceFile> getSpaceFiles(final Long spaceId) {
        List<SpaceFile> listSpaceFiles = new ArrayList<>();
        for(SpaceFileModel region : SpaceFileModel.findBySpaceId(spaceId)) {
            listSpaceFiles.add(new SpaceFile(region.getId(), region.getName()));
        }
        return listSpaceFiles;
    }
    
    public static void getFileLocationToDownload(final Long fileId) {

        SpaceFileModel file = SpaceFileModel.findById(fileId);
        if(file == null)
            notFound(Messages.get(I18N.not_found));
        SpaceModel space = file.getSpace();
        PluginModel plugin = space.getPlugin();
        StorageApi api = new StorageApi(plugin.getUrl());

        RemoteFileInfo res = new RemoteFileInfo();
        for(CredentialModel credential : plugin.getListCredentials()) {
            try {
                String credentialStr =
                        credential.getCredentialData().getContentAsString();

                TokenModel token = Authorization.getToken(
                        plugin.getCloudType(),
                        plugin.getStorageWriteScope(),
                        credentialStr);

                Body<PluginStorageFileDownloadModel> body =
                        api.getDownloadUrl(space.getName(), file.getVirtualName());
                PluginStorageFileDownloadModel content =
                        body.getContent();

                if(body.getContent() == null)
                    continue;

                res.setUrl(content.getUrl());
                res.setMethod(content.getMethod());
                res.setName(file.getName());

                Map<String, String> headers =
                        new HashMap<>();
                if(plugin.getAuthType() == AuthenticationType.AUTH_BEARER_TOKEN) {
                    headers.put("Authorization", "Bearer " + token.getToken());
                } else if(plugin.getAuthType() == AuthenticationType.AUTH_SUPER_USER) {
                }
                res.setHeaders(headers);

                break;
            } catch (Exception e) {    
                Logger.warn(e, "Operation cannot be completed with credential [%s]", e.getMessage());                
            }
        }

        renderJSON(res);
    }

    public static void getFileLocationToUpload(final Long spaceId, final String fileName) {

        SpaceModel space = SpaceModel.findById(spaceId);
        if(space == null)
            notFound(Messages.get(I18N.not_found));

        PluginModel plugin = space.getPlugin();
        StorageApi api = new StorageApi(plugin.getUrl());

        RemoteFileInfo res = new RemoteFileInfo();
        for(CredentialModel credential : plugin.getListCredentials()) {
            try {
                String credentialStr =
                        credential.getCredentialData().getContentAsString();

                TokenModel token = Authorization.getToken(
                        plugin.getCloudType(),
                        plugin.getStorageWriteScope(),
                        credentialStr);

                String virtualName = SpaceFileModel.generateVirtualName(fileName);
                Body<PluginStorageFileUploadModel> body =
                        api.getUploadUrl(space.getName(), virtualName);
                PluginStorageFileUploadModel content =
                        body.getContent();

                if(body.getContent() == null)
                    continue;

                res.setUrl(content.getUrl());
                res.setMethod(content.getMethod());
                res.setName(virtualName);

                Map<String, String> headers =
                        new HashMap<>();
                if(plugin.getAuthType() == AuthenticationType.AUTH_BEARER_TOKEN) {
                    headers.put("Authorization", "Bearer " + token.getToken());
                } else if(plugin.getAuthType() == AuthenticationType.AUTH_SUPER_USER) {
                }
                res.setHeaders(headers);

                break;
            } catch (Exception e) {
                Logger.warn(e, "Operation cannot be completed with credential [%s]", e.getMessage());                
            }
        }

        renderJSON(res);
    }
}

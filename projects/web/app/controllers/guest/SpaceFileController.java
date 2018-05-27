package controllers.guest;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import app.client.StorageApi;
import app.common.Authorization;
import app.models.Body;
import app.models.PluginStorageFileUploadModel;
import app.models.security.TokenModel;
import common.constants.I18N;
import controllers.CRUD.For;
import controllers.Check;
import controllers.adm.BaseAdminController;
import models.CredentialModel;
import models.PluginModel;
import models.SpaceFileModel;
import models.SpaceModel;
import play.i18n.Messages;

@For(SpaceFileModel.class)
@Check("/list/space/files")
public class SpaceFileController extends BaseAdminController {

    public static class UploadFileInfo {

        private String url;
        private String method;
        private Map<String, String> headers;

        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public String getMethod() {
            return method;
        }
        public void setMethod(String method) {
            this.method = method;
        }
        public Map<String, String> getHeaders() {
            return headers;
        }
        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }
    }

    public static void getFileLocationToUpload(final Long spaceId, final String fileName) {

        try {
            SpaceModel space = SpaceModel.findById(spaceId);
            if(space == null)
                notFound(Messages.get(I18N.plugin_not_found));

            PluginModel plugin = space.getPlugin();
            StorageApi api = new StorageApi(plugin.getUrl());

            UploadFileInfo res = new UploadFileInfo();
            for(CredentialModel credential : plugin.getListCredentials()) {
                try {
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(credential.getCredentialData().get(), writer, "UTF-8");

                    TokenModel token = Authorization.getToken(
                            plugin.getCloudType(),
                            plugin.getStorageWriteScope(),
                            writer.toString());

                    String virtualName = SpaceFileModel.generateVirtualName(fileName);
                    Body<PluginStorageFileUploadModel> body =
                            api.getUploadUrl(space.getName(), virtualName);
                    PluginStorageFileUploadModel content =
                            body.getContent();

                    if(body.getContent() == null)
                        continue;

                    res.setUrl(content.getUrl());
                    res.setMethod(content.getMethod());

                    Map<String, String> headers =
                            new HashMap<>();
                    // TODO: treatment for other authentication types
                    headers.put("Authorization", "Bearer " + token.getToken());
                    res.setHeaders(headers);

                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            renderJSON(res);
        } catch (Exception e) {
            e.printStackTrace();
            notFound(Messages.get(I18N.plugin_not_found));
        }

    }
}

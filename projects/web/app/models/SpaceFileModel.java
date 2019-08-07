package models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

import app.client.StorageApi;
import app.common.Authorization;
import app.models.Body;
import app.models.PluginInfoModel.AuthenticationType;
import app.models.PluginStorageFileDownloadModel;
import app.models.PluginStorageFileUploadModel;
import app.models.RemoteFileInfo;
import app.models.security.TokenModel;
import play.Logger;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_space_file")
public class SpaceFileModel extends GenericModel {

    private static SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyyMMddHHmmssSSS");

    @Id
    @GeneratedValue
    private Long id;
    @Expose(serialize = false)
    @Required
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private SpaceModel space;
    @Required
    @MaxSize(100)
    private String virtualName;
    @Required
    @MaxSize(100)
    private String name;
    @MaxSize(200)
    private String publicUrl;

    public static class SpaceFile {

        private Long id;
        private String name;

        public SpaceFile(final Long id, final String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
        public void setName(final String name) {
            this.name = name;
        }
        public Long getId() {
            return this.id;
        }
        public void setId(final Long id) {
            this.id = id;
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data accessing
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static List<SpaceFileModel> findBySpaceId(final Long spaceId) {
        return SpaceFileModel.find(
                " SELECT spaceFile "
                        + " FROM SpaceFileModel spaceFile "
                        + " WHERE spaceFile.space.id = ?1"
                        + " ORDER BY spaceFile.name", spaceId).fetch();
    }

    public static List<SpaceFile> getSpaceFiles(final Long spaceId) {
        final List<SpaceFile> listSpaceFiles = new ArrayList<>();
        for(final SpaceFileModel region : SpaceFileModel.findBySpaceId(spaceId)) {
            listSpaceFiles.add(new SpaceFile(region.getId(), region.getName()));
        }
        return listSpaceFiles;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Long getId() {
        return this.id;
    }
    public void setId(final Long id) {
        this.id = id;
    }
    public SpaceModel getSpace() {
        return this.space;
    }
    public void setSpace(final SpaceModel space) {
        this.space = space;
    }
    public String getName() {
        return this.name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public String getVirtualName() {
        return this.virtualName;
    }
    public void setVirtualName(final String virtualName) {
        this.virtualName = virtualName;
    }
    public String getPublicUrl() {
        return this.publicUrl;
    }
    public void setPublicUrl(final String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public static String generateVirtualName(final String fileName) {
        String virtualName = DATE_FORMAT.format(new Date());
        virtualName += "_" + Math.abs(fileName.hashCode());
        return virtualName;
    }

    public static RemoteFileInfo getDownloadFileInfo(final Long fileId) {

        RemoteFileInfo res = null;

        try {
            final SpaceFileModel file = SpaceFileModel.findById(fileId);
            if(file == null) {
                return null;
            }
            final SpaceModel space = file.getSpace();
            final PluginModel plugin = space.getPlugin();
            final StorageApi api = new StorageApi(plugin.getUrl());

            final CredentialModel credential = space.getCredential();

            final String credentialStr =
                    credential.getCredentialData().getContentAsString();

            final TokenModel token = Authorization.getToken(
                    plugin.getCloudType(),
                    plugin.getStorageWriteScope(),
                    credentialStr);

            final Body<PluginStorageFileDownloadModel> body = api.getDownloadUrl(
                    space.getName(),
                    file.getVirtualName());
            final PluginStorageFileDownloadModel content = body.getContent();

            if(content == null) {
                return null;
            }

            res = new RemoteFileInfo();
            res.setUrl(content.getUrl());
            res.setMethod(content.getMethod());
            res.setName(file.getName());

            final Map<String, String> headers =
                    new HashMap<>();
            if(plugin.getAuthType() == AuthenticationType.AUTH_BEARER_TOKEN) {
                headers.put("Authorization", "Bearer " + token.getToken());
            } else if(plugin.getAuthType() == AuthenticationType.AUTH_SUPER_USER) {
            }
            res.setHeaders(headers);

        } catch (final Exception e) {
            Logger.warn(e, "Operation cannot be completed with credential [%s]", e.getMessage());
        }

        return res;
    }

    public static RemoteFileInfo getUploadFileInfo(final Long spaceId, final String fileName) {
        final SpaceModel space = SpaceModel.findById(spaceId);
        if(space == null) {
            return null;
        }
        final String virtualName = SpaceFileModel.generateVirtualName(fileName);
        return getUploadFileInfo(space, virtualName);
    }

    public static RemoteFileInfo getUploadFileInfo(final SpaceModel space, final String virtualName) {

        RemoteFileInfo res = null;

        try {

            final PluginModel plugin = space.getPlugin();
            final StorageApi api = new StorageApi(plugin.getUrl());

            final CredentialModel credential = space.getCredential();

            final String credentialStr =
                    credential.getCredentialData().getContentAsString();

            final TokenModel token = Authorization.getToken(
                    plugin.getCloudType(),
                    plugin.getStorageWriteScope(),
                    credentialStr);

            final Body<PluginStorageFileUploadModel> body =
                    api.getUploadUrl(space.getName(), virtualName);
            final PluginStorageFileUploadModel content =
                    body.getContent();

            if(body.getContent() == null) {
                return null;
            }

            res = new RemoteFileInfo();
            res.setUrl(content.getUrl());
            res.setMethod(content.getMethod());
            res.setName(virtualName);

            final Map<String, String> headers = new HashMap<>();
            if(plugin.getAuthType() == AuthenticationType.AUTH_BEARER_TOKEN) {
                headers.put("Authorization", "Bearer " + token.getToken());
            }
            res.setHeaders(headers);
        } catch (final Exception e) {
            Logger.warn(e, "Operation cannot be completed with credential [%s]", e.getMessage());
        }

        return res;
    }
}

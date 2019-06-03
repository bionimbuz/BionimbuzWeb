package models;

import java.text.SimpleDateFormat;
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
import jobs.helpers.TokenHelper;
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
        
        public SpaceFile(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public final String getName() {
            return name;
        }
        public final void setName(String name) {
            this.name = name;
        }
        public final Long getId() {
            return id;
        }
        public final void setId(Long id) {
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
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public SpaceModel getSpace() {
        return space;
    }
    public void setSpace(SpaceModel space) {
        this.space = space;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getVirtualName() {
        return virtualName;
    }
    public void setVirtualName(String virtualName) {
        this.virtualName = virtualName;
    }
    public String getPublicUrl() {
        return publicUrl;
    }
    public void setPublicUrl(String publicUrl) {
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
            SpaceFileModel file = SpaceFileModel.findById(fileId);
            if(file == null)
                return null;
            SpaceModel space = file.getSpace();
            PluginModel plugin = space.getPlugin();
            StorageApi api = new StorageApi(plugin.getUrl());
    
            CredentialModel credential = space.getCredential();        
        
            String credentialStr =
                    credential.getCredentialData().getContentAsString();

            TokenModel token = Authorization.getToken(
                    plugin.getCloudType(),
                    plugin.getStorageWriteScope(),
                    credentialStr);

            token = TokenHelper.update_identity(plugin.getCloudType(), token, credentialStr);

            Body<PluginStorageFileDownloadModel> body =
                    api.getDownloadUrl(space.getName(), file.getVirtualName());
            PluginStorageFileDownloadModel content =
                    body.getContent();

            if(body.getContent() == null)
                return null;

            res = new RemoteFileInfo();
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
            
        } catch (Exception e) {    
            Logger.warn(e, "Operation cannot be completed with credential [%s]", e.getMessage());                
        }

        return res;
    }    

    public static RemoteFileInfo getUploadFileInfo(final Long spaceId, final String fileName) {        
        SpaceModel space = SpaceModel.findById(spaceId);
        if(space == null)
            return null;
        String virtualName = SpaceFileModel.generateVirtualName(fileName);
        return getUploadFileInfo(space, virtualName);
    }
    
    public static RemoteFileInfo getUploadFileInfo(SpaceModel space, final String virtualName) {

        RemoteFileInfo res = null;
        
        try {
    
            PluginModel plugin = space.getPlugin();
            StorageApi api = new StorageApi(plugin.getUrl());
    
            CredentialModel credential = space.getCredential(); 
    
            String credentialStr =
                    credential.getCredentialData().getContentAsString();

            TokenModel token = Authorization.getToken(
                    plugin.getCloudType(),
                    plugin.getStorageWriteScope(),
                    credentialStr);

            token = TokenHelper.update_identity(plugin.getCloudType(), token, credentialStr);

            Body<PluginStorageFileUploadModel> body =
                    api.getUploadUrl(space.getName(), virtualName);
            PluginStorageFileUploadModel content =
                    body.getContent();

            if(body.getContent() == null)
                return null;

            res = new RemoteFileInfo();
            res.setUrl(content.getUrl());
            res.setMethod(content.getMethod());
            res.setName(virtualName);

            Map<String, String> headers = new HashMap<>();
            if(plugin.getAuthType() == AuthenticationType.AUTH_BEARER_TOKEN) {
                headers.put("Authorization", "Bearer " + token.getToken());
            }
            res.setHeaders(headers);
        } catch (Exception e) {
            Logger.warn(e, "Operation cannot be completed with credential [%s]", e.getMessage());                
        }

        return res;
    }
}

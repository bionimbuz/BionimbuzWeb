package controllers.adm;

import java.io.StringWriter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.googlecloud.GoogleCredentialsFromJson;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApiMetadata;
import org.jclouds.oauth.v2.AuthorizationApi;
import org.jclouds.oauth.v2.config.OAuthProperties;
import org.jclouds.oauth.v2.domain.Claims;
import org.jclouds.oauth.v2.domain.Token;

import com.google.common.base.Supplier;

import app.client.ImageApi;
import app.client.PluginApi;
import app.common.Authorization;
import app.common.GlobalConstants;
import app.models.Body;
import app.models.security.TokenModel;
import common.constants.I18N;
import controllers.CRUD.For;
import controllers.Check;
import models.CredentialModel;
import models.ImageModel;
import models.PluginModel;
import play.i18n.Messages;
import retrofit2.Call;

@For(ImageModel.class)
@Check("/adm/images")
public class ImageController extends BaseAdminController {
    
    public static void searchImages(final Long pluginId) {
        try {
            PluginModel plugin = PluginModel.findById(pluginId);
            if(plugin == null)
                notFound(Messages.get(I18N.plugin_not_found));

            List<ImageModel> listModels = new ArrayList<ImageModel>();            
            PluginApi pluginApi = new PluginApi(plugin.getUrl());
            ImageApi imageApi = pluginApi.createApi(ImageApi.class);  
            
            for(CredentialModel credential : plugin.getListCredentials()) {
                try {
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(credential.getCredentialData().get(), writer, "UTF-8");
                    
                    TokenModel token = getToken(
                            plugin.getCloudType(),
                            plugin.getReadScope(),
                            writer.toString());
                    Call<Body<List<app.models.ImageModel>>> call = 
                            imageApi.listImages(
                                    GlobalConstants.API_VERSION,
                                    token.getToken(), 
                                    token.getIdentity());       
                    Body<List<app.models.ImageModel>> body = call.execute().body();   
                    if(body.getContent() == null || body.getContent().isEmpty())
                        continue;                    
                    for(app.models.ImageModel image : body.getContent()) {
                        listModels.add(new ImageModel(
                                image.getName(),
                                image.getUrl()));
                    }                    
                } catch (Exception e) {
                    e.printStackTrace();                    
                }
            } 

            renderJSON(listModels);            
        } catch (Exception e) {
            e.printStackTrace();
            notFound(Messages.get(I18N.plugin_not_found));
        }
    }
    
    private static Supplier<Credentials> getCredentialSuplier( 
            final String cloudType,
            final String credentialContent) {
        if(cloudType == "google-compute-engine") {     
            return new GoogleCredentialsFromJson(credentialContent);            
        }        
        return null;
    }
    
    private static Properties getProperties(
            final String cloudType) {
        if(cloudType == "google-compute-engine") {     
            return GoogleComputeEngineApiMetadata.defaultProperties();            
        }        
        return null;
    }
    
    public static TokenModel getToken(
            final String cloudType,
            final String scope,
            final String credentialContent
            ) throws Exception {
        
        Properties properties = getProperties(cloudType);
        Supplier<Credentials> credentialSuplier = 
                getCredentialSuplier(cloudType, credentialContent);
        
        final String aud = properties.getProperty(OAuthProperties.AUDIENCE);
        int now = (int)ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();        

        Claims claims = Claims.create( //
                credentialSuplier.get().identity, // iss 
                scope, // scope
                aud, // aud
                now + GlobalConstants.TOKEN_LIFETIME_SECONDS, // placeholder exp for the cache
                now // placeholder iat for the cache
            );
        
        try(AuthorizationApi api = 
                Authorization.createApi(
                        credentialSuplier, cloudType)) {        
            Token token = api.authorize(claims);            
            return new TokenModel(
                    token.accessToken(), 
                    credentialSuplier.get().identity);
        }
    }    
}
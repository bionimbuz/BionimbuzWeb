package app.client;

import java.io.IOException;
import java.util.List;

import com.google.common.net.HttpHeaders;

import app.client.ImageApi.HttpMethods;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginImageModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public class ImageApi extends PluginApi<HttpMethods> {  

    public ImageApi (final String url) {
        super(url, HttpMethods.class);
    }
    
    public Body<List<PluginImageModel>> listImages (
            final String token, 
            final String identity) throws IOException {
        return getHttpMethods()
                .listImages(
                        GlobalConstants.API_VERSION,
                        token, identity)
                .execute().body();
    }
    
    public Body<PluginImageModel> getImage (
            final String token, 
            final String identity,
            final String name) throws IOException {
        return getHttpMethods()
                .getImage(
                        GlobalConstants.API_VERSION,
                        token, identity, name)
                .execute().body();
    }
    
    protected interface HttpMethods {
        @GET(Routes.IMAGES)
        public Call<Body<List<PluginImageModel>>> listImages(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token, 
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity); 
        @GET(Routes.IMAGES_NAME)
        public Call<Body<PluginImageModel>> getImage(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token, 
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
                @Path("name") final String name); 
    }
}

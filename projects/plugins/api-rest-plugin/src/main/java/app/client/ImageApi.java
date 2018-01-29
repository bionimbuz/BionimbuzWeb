package app.client;

import java.io.IOException;
import java.util.List;

import com.google.common.net.HttpHeaders;

import app.client.ImageApi.HttpMethods;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.ImageModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class ImageApi extends PluginApi<HttpMethods> {  

    public ImageApi (final String url) {
        super(url, HttpMethods.class);
    }
    
    public Body<List<ImageModel>> listImages (
            final String token, 
            final String identity) throws IOException {
        return getHttpMethods()
                .listImages(
                        GlobalConstants.API_VERSION,
                        token, identity)
                .execute().body();
    }
    
    protected interface HttpMethods {
        @GET(Routes.IMAGES)
        public Call<Body<List<ImageModel>>> listImages(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token, 
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity); 
    }
}

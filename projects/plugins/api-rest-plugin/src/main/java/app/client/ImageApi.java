package app.client;

import java.util.List;

import com.google.common.net.HttpHeaders;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.ImageModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ImageApi {
    @GET(Routes.IMAGES)
    public Call<Body<List<ImageModel>>> listImages(
            @Header(HttpHeadersCustom.API_VERSION) final String version,
            @Header(HttpHeaders.AUTHORIZATION) final String token, 
            @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity);    
}

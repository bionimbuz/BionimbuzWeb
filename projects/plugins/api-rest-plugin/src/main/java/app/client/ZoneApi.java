package app.client;

import java.io.IOException;
import java.util.List;

import com.google.common.net.HttpHeaders;

import app.client.ZoneApi.HttpMethods;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginZoneModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class ZoneApi  extends PluginApi<HttpMethods> {

    public ZoneApi (final String url) {
        super(url, HttpMethods.class);
    }
    
    public Body<List<PluginZoneModel>> listZones(
            final String token, 
            final String identity) throws IOException
    {
        return getHttpMethods()
                .listZones(
                        GlobalConstants.API_VERSION,
                        token, identity)
                .execute().body();
    }
    
    protected interface HttpMethods {
        @GET(Routes.ZONES)
        public Call<Body<List<PluginZoneModel>>> listZones(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token, 
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity);    
    }
}

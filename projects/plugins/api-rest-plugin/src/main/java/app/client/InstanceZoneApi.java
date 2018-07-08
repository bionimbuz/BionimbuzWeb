package app.client;

import java.io.IOException;
import java.util.List;

import com.google.common.net.HttpHeaders;

import app.client.InstanceZoneApi.HttpMethods;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginInstanceZoneModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class InstanceZoneApi  extends ClientApiVersioned<HttpMethods> {

    public InstanceZoneApi (final String url) {
        super(url, HttpMethods.class);
    }

    public Body<List<PluginInstanceZoneModel>> listInstanceZones(
            final String token,
            final String identity) throws IOException
    {
        return getHttpMethods()
                .listInstanceZones(
                        GlobalConstants.API_VERSION,
                        token, identity)
                .execute().body();
    }

    protected interface HttpMethods {
        @GET(Routes.INSTANCE_ZONES)
        public Call<Body<List<PluginInstanceZoneModel>>> listInstanceZones(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token,
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity);
    }
}

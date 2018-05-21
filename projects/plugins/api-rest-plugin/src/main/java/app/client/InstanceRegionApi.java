package app.client;

import java.io.IOException;
import java.util.List;

import com.google.common.net.HttpHeaders;

import app.client.InstanceRegionApi.HttpMethods;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginInstanceRegionModel;
import app.models.PluginInstanceZoneModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public class InstanceRegionApi  extends PluginApi<HttpMethods> {

    public InstanceRegionApi (final String url) {
        super(url, HttpMethods.class);
    }

    public Body<List<PluginInstanceRegionModel>> listInstanceRegions(
            final String token, 
            final String identity) throws IOException
    {
        return getHttpMethods()
                .listInstanceRegions(
                        GlobalConstants.API_VERSION,
                        token, identity)
                .execute().body();
    }
    
    public Body<List<PluginInstanceZoneModel>> listInstanceRegionsZones(
            final String token, 
            final String identity,
            final String name) throws IOException
    {
        return getHttpMethods()
                .listInstanceRegionsZones(
                        GlobalConstants.API_VERSION,
                        token, identity, name)
                .execute().body();
    }
    
    protected interface HttpMethods {
        @GET(Routes.INSTANCE_REGIONS)
        public Call<Body<List<PluginInstanceRegionModel>>> listInstanceRegions(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token, 
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity); 
        @GET(Routes.INSTANCE_REGIONS_ZONES)
        public Call<Body<List<PluginInstanceZoneModel>>> listInstanceRegionsZones(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token, 
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
                @Path("name") final String name);    
    }
}

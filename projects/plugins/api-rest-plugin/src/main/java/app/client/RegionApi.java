package app.client;

import java.io.IOException;
import java.util.List;

import com.google.common.net.HttpHeaders;

import app.client.RegionApi.HttpMethods;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginRegionModel;
import app.models.PluginZoneModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public class RegionApi  extends PluginApi<HttpMethods> {

    public RegionApi (final String url) {
        super(url, HttpMethods.class);
    }

    public Body<List<PluginRegionModel>> listRegions(
            final String token, 
            final String identity) throws IOException
    {
        return getHttpMethods()
                .listRegions(
                        GlobalConstants.API_VERSION,
                        token, identity)
                .execute().body();
    }
    
    public Body<List<PluginZoneModel>> listRegionsZones(
            final String token, 
            final String identity,
            final String name) throws IOException
    {
        return getHttpMethods()
                .listRegionsZones(
                        GlobalConstants.API_VERSION,
                        token, identity, name)
                .execute().body();
    }
    
    protected interface HttpMethods {
        @GET(Routes.REGIONS)
        public Call<Body<List<PluginRegionModel>>> listRegions(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token, 
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity); 
        @GET(Routes.REGIONS_ZONES)
        public Call<Body<List<PluginZoneModel>>> listRegionsZones(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token, 
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
                @Path("name") final String name);    
    }
}

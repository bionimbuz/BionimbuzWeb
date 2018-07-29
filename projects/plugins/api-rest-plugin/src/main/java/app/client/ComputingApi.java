package app.client;

import java.io.IOException;
import java.util.List;

import com.google.common.net.HttpHeaders;

import app.client.ComputingApi.HttpMethods;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginComputingInstanceModel;
import app.models.PluginComputingRegionModel;
import app.models.PluginComputingZoneModel;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class ComputingApi  extends ClientApiVersioned<HttpMethods> {

    public ComputingApi (final String url) {
        super(url, HttpMethods.class);
    }

    public Body<List<PluginComputingInstanceModel>> createInstances(
            final String token,
            final String identity,
            List<PluginComputingInstanceModel> listModel) throws IOException
    {
        return getHttpMethods()
                .createInstances(
                        GlobalConstants.API_VERSION,
                        token, identity, listModel)
                .execute().body();
    }

    public Body<PluginComputingInstanceModel> getInstance(
            final String token,
            final String identity,
            final String zone,
            final String name) throws IOException
    {
        return getHttpMethods()
                .getInstance(
                        GlobalConstants.API_VERSION,
                        token, identity, zone, name)
                .execute().body();
    }

    public Body<Boolean> deleteInstance(
            final String token,
            final String identity,
            final String zone,
            final String name) throws IOException
    {
        return getHttpMethods()
                .deleteInstance(
                        GlobalConstants.API_VERSION,
                        token, identity, zone, name)
                .execute().body();
    }

    public Body<List<PluginComputingInstanceModel>> listInstances(
            final String token,
            final String identity) throws IOException
    {
        return getHttpMethods()
                .listInstances(
                        GlobalConstants.API_VERSION,
                        token, identity)
                .execute().body();
    }

    public Body<List<PluginComputingRegionModel>> listRegions(
            final String token,
            final String identity) throws IOException
    {
        return getHttpMethods()
                .listRegions(
                        GlobalConstants.API_VERSION,
                        token, identity)
                .execute().body();
    }

    public Body<List<PluginComputingZoneModel>> listRegionZones(
            final String token,
            final String identity,
            final String name) throws IOException
    {
        return getHttpMethods()
                .listRegionZones(
                        GlobalConstants.API_VERSION,
                        token, identity, name)
                .execute().body();
    }
    
    protected interface HttpMethods {
        @POST(Routes.COMPUTING_INSTANCES)
        public Call<Body<List<PluginComputingInstanceModel>>> createInstances(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token,
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
                @retrofit2.http.Body List<PluginComputingInstanceModel> listModel);
        @GET(Routes.COMPUTING_REGIONS_ZONES_INSTANCES_NAME)
        public Call<Body<PluginComputingInstanceModel>> getInstance(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token,
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
                @Path("zone") final String zone,
                @Path("name") final String name);
        @DELETE(Routes.COMPUTING_REGIONS_ZONES_INSTANCES_NAME)
        public Call<Body<Boolean>> deleteInstance(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token,
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
                @Path("zone") final String zone,
                @Path("name") final String name);
        @GET(Routes.COMPUTING_INSTANCES)
        public Call<Body<List<PluginComputingInstanceModel>>> listInstances(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token,
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity);        
        @GET(Routes.COMPUTING_REGIONS)
        public Call<Body<List<PluginComputingRegionModel>>> listRegions(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token,
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity);
        @GET(Routes.COMPUTING_REGIONS_ZONES)
        public Call<Body<List<PluginComputingZoneModel>>> listRegionZones(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token,
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
                @Path("name") final String name);
    }
}

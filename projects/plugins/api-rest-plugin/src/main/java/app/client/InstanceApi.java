package app.client;

import java.io.IOException;
import java.util.List;

import com.google.common.net.HttpHeaders;

import app.client.InstanceApi.HttpMethods;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginInstanceModel;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class InstanceApi  extends PluginApi<HttpMethods> {

    public InstanceApi (final String url) {
        super(url, HttpMethods.class);
    }
    
    public Body<List<PluginInstanceModel>> createInstance(
            final String token, 
            final String identity, 
            List<PluginInstanceModel> listModel) throws IOException
    {
        return getHttpMethods()
                .createInstance(
                        GlobalConstants.API_VERSION,
                        token, identity, listModel)
                .execute().body();
    }
    
    public Body<PluginInstanceModel> getInstance(
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
    
    public Body<Void> deleteInstance(
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
    
    public Body<List<PluginInstanceModel>> listInstances(
            final String token, 
            final String identity) throws IOException
    {
        return getHttpMethods()
                .listInstances(
                        GlobalConstants.API_VERSION,
                        token, identity)
                .execute().body();
    }
    
    protected interface HttpMethods {
        @POST(Routes.INSTANCES)
        public Call<Body<List<PluginInstanceModel>>> createInstance(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token, 
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
                @retrofit2.http.Body List<PluginInstanceModel> listModel);
        @GET(Routes.INSTANCES_ZONE_NAME)
        public Call<Body<PluginInstanceModel>> getInstance(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token, 
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
                @Path("zone") final String zone,
                @Path("name") final String name);
        @DELETE(Routes.INSTANCES_ZONE_NAME)
        public Call<Body<Void>> deleteInstance(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token, 
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
                @Path("zone") final String zone,
                @Path("name") final String name);
        @GET(Routes.INSTANCES)
        public Call<Body<List<PluginInstanceModel>>> listInstances(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token, 
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity);    
    }
}

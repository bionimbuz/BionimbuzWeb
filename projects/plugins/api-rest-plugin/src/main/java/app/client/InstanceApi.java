package app.client;

import java.util.List;

import com.google.common.net.HttpHeaders;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.InstanceModel;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface InstanceApi {
    @POST(Routes.INSTANCES)
    public Call<Body<List<InstanceModel>>> createInstance(
            @Header(HttpHeadersCustom.API_VERSION) final String version,
            @Header(HttpHeaders.AUTHORIZATION) final String token, 
            @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
            @retrofit2.http.Body List<InstanceModel> listModel);
    @GET(Routes.INSTANCES_ZONE_NAME)
    public Call<Body<InstanceModel>> getInstance(
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
    public Call<Body<List<InstanceModel>>> listInstances(
            @Header(HttpHeadersCustom.API_VERSION) final String version,
            @Header(HttpHeaders.AUTHORIZATION) final String token, 
            @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity);    
}

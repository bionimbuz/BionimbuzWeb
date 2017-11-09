package app.client;

import java.util.List;

import com.google.common.net.HttpHeaders;

import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.FirewallModel;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FirewallApi {    
    @POST(Routes.FIREWALLS)
    public Call< Body<FirewallModel> > replaceRule(
            @Header(HttpHeadersCustom.API_VERSION) final String version,
            @Header(HttpHeaders.AUTHORIZATION) final String token, 
            @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
            @retrofit2.http.Body FirewallModel model);
    @GET(Routes.FIREWALLS_NAME) 
    public Call< Body<FirewallModel> > getRule(
            @Header(HttpHeadersCustom.API_VERSION) final String version,
            @Header(HttpHeaders.AUTHORIZATION) final String token, 
            @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
            @Path("name") final String name);
    @DELETE(Routes.FIREWALLS_NAME)
    public Call< Body<Void> > deleteRule(
            @Header(HttpHeadersCustom.API_VERSION) final String version,
            @Header(HttpHeaders.AUTHORIZATION) final String token,  
            @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity, 
            @Path("name") final String name);
    @GET(Routes.FIREWALLS) 
    public Call< Body<List<FirewallModel>> > listRules(
            @Header(HttpHeadersCustom.API_VERSION) final String version,
            @Header(HttpHeaders.AUTHORIZATION) final String token, 
            @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity);
}

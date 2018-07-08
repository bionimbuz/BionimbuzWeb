package app.client;

import java.io.IOException;
import java.util.List;

import com.google.common.net.HttpHeaders;

import app.client.FirewallApi.HttpMethods;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginFirewallModel;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class FirewallApi extends ClientApiVersioned<HttpMethods> {

    public FirewallApi (final String url) {
        super(url, HttpMethods.class);
    }

    public Body<PluginFirewallModel> replaceRule(
            final String token,
            final String identity,
            PluginFirewallModel model) throws IOException
    {
        return getHttpMethods()
                .replaceRule(
                        GlobalConstants.API_VERSION,
                        token, identity, model)
                .execute().body();
    }

    public Body<PluginFirewallModel> getRule(
            final String token,
            final String identity,
            final String name) throws IOException
    {
        return getHttpMethods()
                .getRule(
                        GlobalConstants.API_VERSION,
                        token, identity, name)
                .execute().body();
    }

    public Body<Boolean> deleteRule(
            final String token,
            final String identity,
            final String name) throws IOException
    {
        return getHttpMethods()
                .deleteRule(
                        GlobalConstants.API_VERSION,
                        token, identity, name)
                .execute().body();
    }

    public Body<List<PluginFirewallModel>> listRules (
            final String token,
            final String identity) throws IOException {
        return getHttpMethods()
                .listRules(
                        GlobalConstants.API_VERSION,
                        token, identity)
                .execute().body();
    }

    protected interface HttpMethods {
        @POST(Routes.FIREWALLS)
        public Call< Body<PluginFirewallModel> > replaceRule(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token,
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
                @retrofit2.http.Body PluginFirewallModel model);
        @GET(Routes.FIREWALLS_NAME)
        public Call< Body<PluginFirewallModel> > getRule(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token,
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
                @Path("name") final String name);
        @DELETE(Routes.FIREWALLS_NAME)
        public Call< Body<Boolean> > deleteRule(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token,
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
                @Path("name") final String name);
        @GET(Routes.FIREWALLS)
        public Call< Body<List<PluginFirewallModel>> > listRules(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token,
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity);
    }
}

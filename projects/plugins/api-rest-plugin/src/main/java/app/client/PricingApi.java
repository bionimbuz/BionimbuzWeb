package app.client;

import java.io.IOException;

import com.google.common.net.HttpHeaders;

import app.client.PricingApi.HttpMethods;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PricingModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class PricingApi extends PluginApi<HttpMethods> {    
        
    public PricingApi (final String url) {
        super(url, HttpMethods.class);
    }
    
    public Body<PricingModel> getPricing(
            final String token, 
            final String identity) throws IOException
    {
        return getHttpMethods()
                .getPricing(
                        GlobalConstants.API_VERSION,
                        token, identity)
                .execute().body();
    }
    
    protected interface HttpMethods {
        @GET(Routes.FIREWALLS_NAME) 
        public Call< Body<PricingModel> > getPricing(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token, 
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity);
    }
}

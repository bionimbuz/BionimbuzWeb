package app.client;

import java.io.IOException;

import app.client.PricingApi.HttpMethods;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PriceTableModel;
import app.models.PriceTableStatusModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class PricingApi extends PluginApi<HttpMethods> {    
        
    public PricingApi (final String url) {
        super(url, HttpMethods.class);
    }
    
    public Body<PriceTableModel> getPricing() throws IOException
    {
        return getHttpMethods()
                .getPricing(GlobalConstants.API_VERSION)
                .execute().body();
    }
    public Body<PriceTableStatusModel> getPricingStatus() throws IOException
    {
        return getHttpMethods()
                .getPricingStatus(GlobalConstants.API_VERSION)
                .execute().body();
    }
    
    protected interface HttpMethods {
        @GET(Routes.PRICING) 
        public Call< Body<PriceTableModel> > getPricing(
                @Header(HttpHeadersCustom.API_VERSION) final String version);
        @GET(Routes.PRICING_STATUS) 
        public Call< Body<PriceTableStatusModel> > getPricingStatus(
                @Header(HttpHeadersCustom.API_VERSION) final String version);
    }
}

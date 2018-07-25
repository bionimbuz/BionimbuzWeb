package app.client;

import java.io.IOException;

import app.client.PricingApi.HttpMethods;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginPriceTableModel;
import app.models.PluginPriceTableStatusModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class PricingApi extends ClientApiVersioned<HttpMethods> {

    public PricingApi (final String url) {
        super(url, HttpMethods.class);
    }

    public Body<PluginPriceTableModel> getPricing() throws IOException
    {
        return getHttpMethods()
                .getPricing(GlobalConstants.API_VERSION)
                .execute().body();
    }
    public Body<PluginPriceTableStatusModel> getPricingStatus() throws IOException
    {
        return getHttpMethods()
                .getPricingStatus(GlobalConstants.API_VERSION)
                .execute().body();
    }

    protected interface HttpMethods {
        @GET(Routes.PRICING)
        public Call< Body<PluginPriceTableModel> > getPricing(
                @Header(HttpHeadersCustom.API_VERSION) final String version);
        @GET(Routes.PRICING_STATUS)
        public Call< Body<PluginPriceTableStatusModel> > getPricingStatus(
                @Header(HttpHeadersCustom.API_VERSION) final String version);
    }
}

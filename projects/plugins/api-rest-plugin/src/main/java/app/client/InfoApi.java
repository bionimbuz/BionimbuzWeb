package app.client;

import java.io.IOException;

import app.client.InfoApi.HttpMethods;
import app.common.Routes;
import app.models.Body;
import app.models.PluginInfoModel;
import retrofit2.Call;
import retrofit2.http.GET;

public class InfoApi extends ClientApiVersioned<HttpMethods> {

    public InfoApi (final String url) {
        super(url, HttpMethods.class);
    }

    public Body<PluginInfoModel> getInfo() throws IOException
    {
        return getHttpMethods()
                .getInfo()
                .execute().body();
    }

    protected interface HttpMethods {
        @GET(Routes.INFO)
        public Call< Body<PluginInfoModel> > getInfo();
    }
}

package app.client;

import java.io.IOException;

import app.client.InfoApi.HttpMethods;
import app.common.Routes;
import app.models.Body;
import app.models.InfoModel;
import retrofit2.Call;
import retrofit2.http.GET;

public class InfoApi extends PluginApi<HttpMethods> {
    
    public InfoApi (final String url) {
        super(url, HttpMethods.class);
    }

    public Body<InfoModel> getInfo() throws IOException
    {
        return getHttpMethods()
                .getInfo()
                .execute().body();
    }
    
    protected interface HttpMethods {
        @GET(Routes.INFO)
        public Call< Body<InfoModel> > getInfo();
    }
}

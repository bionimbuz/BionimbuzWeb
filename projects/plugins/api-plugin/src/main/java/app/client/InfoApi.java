package app.client;

import app.common.Routes;
import app.models.Body;
import app.models.InfoModel;
import retrofit2.Call;
import retrofit2.http.GET;

public interface InfoApi {
    @GET(Routes.INFO)
    public Call< Body<InfoModel> > getInfo();
}

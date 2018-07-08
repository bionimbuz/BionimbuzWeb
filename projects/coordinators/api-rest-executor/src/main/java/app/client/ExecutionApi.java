package app.client;

import java.io.IOException;

import app.client.ExecutionApi.HttpMethods;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.Command;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class ExecutionApi extends ClientApiVersioned<HttpMethods> {

    public ExecutionApi (final String url) {
        super(url, HttpMethods.class);
    }

    public Body<Boolean> postCommand(
            final String token,
            final Command command) throws IOException
    {
        return getHttpMethods()
                .createInstance(
                        getAPIVersion(),
                        command)
                .execute().body();
    }

    protected interface HttpMethods {
        @POST(Routes.INSTANCES)
        public Call<Body<Boolean>> createInstance(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @retrofit2.http.Body final Command command);
    }
}

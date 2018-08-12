package app.client;

import java.io.IOException;

import app.client.ExecutionApi.HttpMethods;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.Command;
import app.models.ExecutionStatus;
import app.models.STATUS;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class ExecutionApi extends ClientApiVersioned<HttpMethods> {

    public ExecutionApi (final String url) {
        super(url, HttpMethods.class);
    }

    public Body<Boolean> startExecution(
            final Command command) throws IOException
    {
        return getHttpMethods()
                .startExecution(
                        getAPIVersion(),
                        command)
                .execute().body();
    }
    public Body<ExecutionStatus> getExecutionStatus() throws IOException
    {
        return getHttpMethods()
                .getExecutionStatus(
                        getAPIVersion())
                .execute().body();
    }
    public Body<STATUS> getStatus() throws IOException
    {
        return getHttpMethods()
                .getStatus(
                        getAPIVersion())
                .execute().body();
    }

    protected interface HttpMethods {
        @POST(Routes.EXECUTION_START)
        public Call<Body<Boolean>> startExecution(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @retrofit2.http.Body final Command command);
        @GET(Routes.EXECUTION_STATUS)
        public Call<Body<ExecutionStatus>> getExecutionStatus(
                @Header(HttpHeadersCustom.API_VERSION) final String version);
        @GET(Routes.STATUS)
        public Call<Body<STATUS>> getStatus(
                @Header(HttpHeadersCustom.API_VERSION) final String version);
    }
}

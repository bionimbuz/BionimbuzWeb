package app.client;

import java.io.IOException;

import com.google.common.net.HttpHeaders;

import app.client.StorageApi.HttpMethods;
import app.common.GlobalConstants;
import app.common.HttpHeadersCustom;
import app.common.Routes;
import app.models.Body;
import app.models.PluginStorageModel;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class StorageApi  extends PluginApi<HttpMethods> {

    public StorageApi (final String url) {
        super(url, HttpMethods.class);
    }

    public Body<PluginStorageModel> createSpace(
            final String token,
            final String identity,
            PluginStorageModel model) throws IOException
    {
        return getHttpMethods()
                .createSpace(
                        GlobalConstants.API_VERSION,
                        token, identity, model)
                .execute().body();
    }

    public Body<Void> deleteSpace(
            final String token,
            final String identity,
            final String name) throws IOException
    {
        return getHttpMethods()
                .deleteSpace(
                        GlobalConstants.API_VERSION,
                        token, identity, name)
                .execute().body();
    }

    protected interface HttpMethods {
        @POST(Routes.SPACES)
        public Call<Body<PluginStorageModel>> createSpace(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token,
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
                @retrofit2.http.Body PluginStorageModel model);
        @DELETE(Routes.SPACES_NAME)
        public Call<Body<Void>> deleteSpace(
                @Header(HttpHeadersCustom.API_VERSION) final String version,
                @Header(HttpHeaders.AUTHORIZATION) final String token,
                @Header(HttpHeadersCustom.AUTHORIZATION_ID) final String identity,
                @Path("name") final String name);
    }
}

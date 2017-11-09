package app.client;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PluginApi {
    
    private OkHttpClient client;    
    private Retrofit retrofit;

    public PluginApi(final String url) throws URISyntaxException {        
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS).build();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder().create()))
                .client(client)
                .build();
    }
    
    public <T> T createApi(Class<T> clazz) throws URISyntaxException {
        return retrofit.create(clazz);
    }
}

package app.client;

import java.util.concurrent.TimeUnit;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PluginApi {
    
    private static final int CONNECT_TIMEOUT_SECS = 10;
    private static final int READ_TIMEOUT_SECS = 10;
    
    private OkHttpClient client;    
    private Retrofit retrofit;

    public PluginApi(final String url) {        
        this.client = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECS,TimeUnit.SECONDS).build();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder().create()))
                .client(client)
                .build();
    }
    
    public <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }
}

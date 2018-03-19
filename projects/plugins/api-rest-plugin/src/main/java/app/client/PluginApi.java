package app.client;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

class PluginApi <T> {
    
    private static final int CONNECT_TIMEOUT_SECS = 120;
    private static final int READ_TIMEOUT_SECS = 120;
    
    private OkHttpClient client;    
    private Retrofit retrofit;  
    private T httpMethods;
    
    public PluginApi(final String url, final Class<T> clazz) {        
        this.client = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECS,TimeUnit.SECONDS).build();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
        this.httpMethods = retrofit.create(clazz);
    }    
    
    public T getHttpMethods() {
        return httpMethods;
    }

}

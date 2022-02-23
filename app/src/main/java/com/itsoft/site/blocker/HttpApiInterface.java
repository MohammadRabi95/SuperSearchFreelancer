package com.itsoft.site.blocker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class HttpApiInterface {

    public interface HttpService

    {
        @GET("AppSettingsApi/Get")
        Call<KeywordModel> getKeywordData();
    }

    public static HttpService getHttpService()

    {
        Gson gson = new GsonBuilder().setLenient().create();

        if(client == null) {
            OkHttpClient ok = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(180, TimeUnit.SECONDS)
                    .build();
            client = new Retrofit.Builder()
                    .client(ok)
                    .baseUrl("AppController.getInstance().Url")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return client.create(HttpService.class);
    }

    private static Retrofit client;

}

package com.tringuyen.anzi.network;

import com.tringuyen.anzi.Constants;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tri Nguyen on 11/28/2016.
 */

public class GoogleServiceGenerator {

//    private static HttpLoggingInterceptor logging;
    private static OkHttpClient.Builder httpClientBuilder;
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                        .baseUrl(Constants.GOOGLE_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

    public static <T> T createService(Class<T> serviceClass)
    {
        httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                HttpUrl.Builder builder = chain.request().url().newBuilder();
                builder.addQueryParameter("key",Constants.GOOGLE_PALCE_API_KEY);

                HttpUrl url = builder.build();
                Request.Builder reqBuilder = chain.request().newBuilder();
                reqBuilder.url(url);
                return chain.proceed(reqBuilder.build());
            }
        });
        Retrofit retrofit = builder.client(httpClientBuilder.build()).build();
        return retrofit.create(serviceClass);
    }
}

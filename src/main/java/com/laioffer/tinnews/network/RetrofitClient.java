package com.laioffer.tinnews.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


//chrome(client) -> url -> http -> net call -> json(html) -> java class(render -> website)
//RetrofitClient -> url -> okhttp -> net call -> json -> parse -> java class
public class RetrofitClient {
    private static final String API_KEY = "";
    private static final String BASE_URL = "https://newsapi.org/v2/";
    public static Retrofit newInstance() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor())
                .build();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)//url
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)//http
                .build();
        //okhttp, interceptor不要求掌握
        //interceptor:加入我的app里由10个retrofit call，那么我和apikey之间就像一条双向数据通路，任何在通路上的
        //对象都可以intercept数据包并且加header，我就通过在中间截取，并把所有的请求都加上我申请到的key，
        //这就是interceptor的大致作用
    }
    private static class HeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request request = original
                    .newBuilder()
                    .header("X-Api-Key", API_KEY)
                    .build();
            return chain.proceed(request);
        }
    }
}

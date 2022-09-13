package com.laioffer.tinnews.network;

import com.laioffer.tinnews.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {

    //use @ to let Retrofit implement network interface request at runtime
    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlines(@Query("country") String country);
    //   base_url/top-headlines/country

    @GET("everything")
    Call<NewsResponse> getEverything(
            @Query("q") String query, @Query("pageSize") int pageSize);

}
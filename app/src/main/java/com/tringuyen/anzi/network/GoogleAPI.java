package com.tringuyen.anzi.network;

import com.tringuyen.anzi.model.google.google_detail_activity.GoogleDetailResponse;
import com.tringuyen.anzi.model.google.google_search_activity.GoogleResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Tri Nguyen on 11/28/2016.
 */

public interface GoogleAPI {
    @GET("place/nearbysearch/json")
    Call<GoogleResponse> searchResult(
            @Query("location") String location,
            @Query("keyword") String query );

    @GET("place/details/json")
    Call<GoogleDetailResponse> searchDetailResult(@Query("placeid") String resultID);
}

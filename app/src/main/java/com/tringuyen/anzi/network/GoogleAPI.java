package com.tringuyen.anzi.network;

import com.tringuyen.anzi.model.google.google_detail_activity.GoogleDetailResponse;
import com.tringuyen.anzi.model.google.google_direction_direction.DirectionResponse;
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
            @Query("types") String types,
            @Query("rankby") String rankby,
            @Query("keyword") String query );
//    @Query("types") String types,
    @GET("place/details/json")
    Call<GoogleDetailResponse> searchDetailResult(@Query("placeid") String resultID);


    @GET("directions/json")
    Call<DirectionResponse> getDirection(@Query("origin") String origin, @Query("destination") String destination);
}

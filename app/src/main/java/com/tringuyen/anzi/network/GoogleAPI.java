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
            @Query("keyword") String query );

    @GET("place/details/json")
    Call<GoogleDetailResponse> searchDetailResult(@Query("placeid") String resultID);

    @GET("place/details/json")
    Call<GoogleDetailResponse> searchDetailBySingleCategory(@Query("placeid") String placeID, @Query("types") String type);

    @GET("directions/json")
    Call<DirectionResponse> getDirection(@Query("origin") String origin, @Query("destination") String destination);
}

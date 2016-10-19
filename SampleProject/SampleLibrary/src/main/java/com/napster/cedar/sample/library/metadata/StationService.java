package com.napster.cedar.sample.library.metadata;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface StationService {

    @GET("/v2.0/stations/top")
    public void getTopStations(
            @Query(Constants.APIKEY) String apikey,
            @Query(Constants.LIMIT) int limit,
            @Query(Constants.OFFSET) int offset,
            Callback<Stations> callback);

}

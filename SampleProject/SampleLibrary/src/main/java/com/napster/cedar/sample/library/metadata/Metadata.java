package com.napster.cedar.sample.library.metadata;

import retrofit.Callback;
import retrofit.RestAdapter;


public class Metadata {

    TrackService trackService;
    StationService stationService;
    String apiKey;

    public Metadata(String apiKey) {
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(com.napster.cedar.sample.library.Constants.ENDPOINT_HTTP).build();
        trackService = adapter.create(TrackService.class);
        stationService = adapter.create(StationService.class);
        this.apiKey = apiKey;
    }

    public void getTopTracks(int limit, int offset, Callback<Tracks> callback) {
        trackService.getTopTracks(apiKey, limit, offset, callback);
    }

    public void getTopStations(int limit, int offset, Callback<Stations> callback) {
        stationService.getTopStations(apiKey, limit, offset, callback);
    }

    public TrackService getTrackService() {
        return trackService;
    }

}

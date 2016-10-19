package com.napster.cedar.sample.library.metadata;

import com.google.gson.annotations.SerializedName;
import com.napster.cedar.player.data.Track;

import java.util.List;

public class Tracks {

    @SerializedName("tracks")
    public final List<Track> tracks;

    public Tracks(List<Track> tracks) {
        this.tracks = tracks;
    }

}
package com.napster.cedar.sample.library.metadata;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Stations implements Serializable {

    @SerializedName("stations")
    public final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }
}
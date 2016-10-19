package com.napster.cedar.sample.sample.player;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.napster.cedar.sample.library.MainActivity;
import com.napster.cedar.sample.library.metadata.Station;

public class StationPlayerActivity extends MainActivity {

    public static final String EXTRA_STATION = "station";
    StationPlayerFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Station station = (Station) getIntent().getExtras().get(EXTRA_STATION);
        getSupportActionBar().setTitle(station.name);
    }

    @Override
    protected Fragment getFragment() {
        if(fragment == null) {
            fragment = new StationPlayerFragment();
            fragment.setArguments(getIntent().getExtras());
        }
        return fragment;
    }

    @Override
    protected void onLogin() {
        refreshActiveFragment();
    }

    @Override
    protected void onLogout() {
        removeActiveFragment();
    }

}

package com.napster.cedar.sample.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.napster.cedar.sample.R;
import com.napster.cedar.sample.library.MainActivity;

public class TopTenActivity extends MainActivity {

    TopTenFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.top_ten_tracks));
    }

    @Override
    protected Fragment getFragment() {
        if(fragment == null) {
            fragment = new TopTenFragment();
        }
        return fragment;
    }

    @Override
    protected void onLogin() {
        //NOTHING
    }

    @Override
    protected void onLogout() {
        //NOTHING
    }

}

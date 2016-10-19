package com.napster.cedar.sample.sample.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.napster.cedar.sample.R;
import com.napster.cedar.sample.library.MainActivity;

public class StationListActivity extends MainActivity {

    StationListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.top_stations));
    }

    @Override
    protected Fragment getFragment() {
        if(fragment == null) {
            fragment = new StationListFragment();
            fragment.setArguments(getIntent().getExtras());
        }
        return fragment;
    }

    @Override
    protected void onLogin() {
    }

    @Override
    protected void onLogout() {
    }

}

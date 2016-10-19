package com.napster.cedar.sample.sample.player;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.napster.cedar.Napster;
import com.napster.cedar.NapsterError;
import com.napster.cedar.player.data.Track;
import com.napster.cedar.sample.sample.StationPlayerSampleApplication;
import com.napster.cedar.sample.R;
import com.napster.cedar.sample.library.PlayerFragment;
import com.napster.cedar.sample.library.Utils;
import com.napster.cedar.CompletionCallback;
import com.napster.cedar.sample.library.metadata.Station;
import com.napster.cedar.station.PreviewTracksChangeListener;
import com.napster.cedar.station.StationPlayer;
import com.napster.cedar.station.data.StationTrackLikedState;
import com.napster.cedar.station.data.StationTuningParams;

import java.util.List;

public class StationPlayerFragment extends PlayerFragment implements PreviewTracksChangeListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, StationPlayer.CreationCallback {

    StationPlayer stationPlayer;
    int colorHighlighted, colorNormal;
    View vLike, vDislike;
    TextView tvSkipsLeft;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = getResources();
        colorHighlighted = res.getColor(R.color.accent_light);
        colorNormal = res.getColor(android.R.color.transparent);

        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout playerButtonRow = (LinearLayout) view.findViewById(R.id.mini_player_button_row);
        appendAdditionalViews(playerButtonRow);

        tvSkipsLeft = (TextView) view.findViewById(R.id.skips_left);
        vLike.setOnClickListener(this);
        vDislike.setOnClickListener(this);
        tracksListView.setOnItemClickListener(this);
        tracksListView.setOnItemLongClickListener(this);
        miniPlayerContainer.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Station station = (Station) getArguments().get(StationPlayerActivity.EXTRA_STATION);
        setupStationPlayer(station);
    }

    private void setupStationPlayer(Station station) {
        StationPlayerSampleApplication app = (StationPlayerSampleApplication) getActivity().getApplication();
        StationPlayer stationPlayerFromApp = app.getStationPlayer();
        if(stationPlayerFromApp != null && (station.id.equals(stationPlayerFromApp.getStationId()))) {
            if(app.getSessionManager().isSessionOpen()) {
                onStationPlayerCreationSuccess(stationPlayerFromApp);
                onPreviewTracksChanged();
            } else {
                onStationPlayerCreationError(new NapsterError(NapsterError.Type.AUTHENTICATION));
            }
        } else {
            Napster napster = app.getNapster();
            napster.createStationPlayer(station.id, this);
        }
        updateActionBarTitle(station.name);
    }

    private void updateActionBarTitle(String title) {
        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(title);
    }

    @Override
    public void onPause() {
        if(stationPlayer != null) {
            stationPlayer.setPreviewTracksChangeListener(PreviewTracksChangeListener.EMPTY);
        }
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(stationPlayer == null) {
            return;
        }
        inflater.inflate(R.menu.menu_create, menu);
        if(stationPlayer.canTune()) {
            inflater.inflate(R.menu.menu_tune, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_item_tune) {
            showTuneDialog();
            return true;
        } else if(id == R.id.menu_item_artist_radio) {
            loadArtistStation(stationPlayer.getCurrentTrack());
            return true;
        } else if(id == R.id.menu_item_track_radio) {
            loadTrackStation(stationPlayer.getCurrentTrack());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTuneDialog() {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_tune, null);
        final SeekBar varietySeekBar = (SeekBar) dialogView.findViewById(R.id.tune_variety);
        final SeekBar popularitySeekBar = (SeekBar) dialogView.findViewById(R.id.tune_popularity);

        StationTuningParams currentTuning = stationPlayer.getStationTuningParams();
        varietySeekBar.setProgress((int) (currentTuning.variety * 100));
        popularitySeekBar.setProgress((int) (currentTuning.popularity * 100));

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stationPlayer.tune(new StationTuningParams(varietySeekBar.getProgress(), popularitySeekBar.getProgress()), tuningCompleteCallback);
            }
        };

        AlertDialog tuneDialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setPositiveButton(getString(android.R.string.ok), onClickListener)
                .setNegativeButton(getString(android.R.string.cancel), null)
                .create();

        tuneDialog.show();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == vLike) {
            updateLikeButtons(StationTrackLikedState.LIKED);
            stationPlayer.setCurrentTrackLikedState(StationTrackLikedState.LIKED, tuningCompleteCallback);
        } else if(view == vDislike) {
            updateLikeButtons(StationTrackLikedState.DISLIKED);
            stationPlayer.setCurrentTrackLikedState(StationTrackLikedState.DISLIKED, tuningCompleteCallback);
        } else if(view == btnPrevious) {
            btnPrevious.setEnabled(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        //need to skip current + all Previews before this one
        if(stationPlayer.getNumberOfSkipsLeft() > pos) {
            clearTrackInfo();
            stationPlayer.playPreviewTrackAtIndex(pos);
            onSkipCountUpdate();
        } else {
            showNotEnoughSkipsLeftMessage();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int pos, long l) {
        AlertDialog trackDialog = new AlertDialog.Builder(getActivity())
                .setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(stationPlayer.canSkipForward()) {
                            stationPlayer.removePreviewTrackAtIndex(pos);
                            onSkipCountUpdate();
                        } else {
                            showNotEnoughSkipsLeftMessage();
                        }
                    }
                })
                .create();

        trackDialog.show();
        return true;
    }

    private void loadTrackStation(Track track) {
        setupStationPlayer(createStationWith(track.id, track.name));
    }

    private void loadArtistStation(Track track) {
        setupStationPlayer(createStationWith(track.links.artists.ids.get(0), track.artistName));
    }

    private Station createStationWith(String id, String name) {
        //TODO might change this one day
        return new Station(id, name + " Radio", "", "", "", "", new Station.Links(null, null));
    }

    @Override
    public void onPreviewTracksChanged() {
        updateTrackInfo(stationPlayer.getCurrentTrack());
        List<Track> nextTracks = stationPlayer.getPreviewTracks();
        trackAdapter.updateTracks(nextTracks);
        onCapabilitiesUpdate();
    }

    private void onCapabilitiesUpdate() {
        getActivity().invalidateOptionsMenu();
        updateLikeButtonsVisibility();
        updateSkipForwardButtonVisibility();
        updateSkipBackwardButtonVisibility();
    }

    @Override
    public void onTrackLoadError(NapsterError napsterError) {
        if(isLoginError(napsterError)) {
            onLoginError();
        } else {
            //your retry logic here
            //retry track load with stationPlayer.forceLoadTracks(true);
        }
    }

    private boolean isLoginError(NapsterError napsterError) {
        return napsterError.type == NapsterError.Type.AUTHENTICATION;
    }

    private void onLoginError() {
        Toast.makeText(getActivity(), getString(R.string.please_login), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPlaybackStarted() {
        super.onPlaybackStarted();
        if(stationPlayer == null) {
            return;
        }
        updateLikeButtonsVisibility();
        updateSkipBackwardButtonVisibility();
        updateLikeButtons(stationPlayer.getCurrentTrackLikedState());
    }

    @Override
    protected void onPlaybackStopped() {
    }

    @Override
    protected void play() {
        stationPlayer.play();
        updateSkipCountView();
    }

    @Override
    protected void stop() {
        player.stop();
    }

    @Override
    protected void pause() {
        player.pause();
    }

    @Override
    protected void playNextTrack() {
        stationPlayer.skipForward();
        onSkipCountUpdate();
    }

    @Override
    protected void playPreviousTrack() {
        stationPlayer.skipBackward();
    }

    private void appendAdditionalViews(LinearLayout root) {
        vLike = getActivity().getLayoutInflater().inflate(R.layout.btn_like, root, false);
        root.addView(vLike, 0);
        vDislike = getActivity().getLayoutInflater().inflate(R.layout.btn_dislike, root, false);
        root.addView(vDislike, root.getChildCount());
    }

    private void onSkipCountUpdate() {
        if (getActivity() == null || stationPlayer.hasUnlimitedSkips()) {
            return;
        }
        updateSkipCountView();
        updateSkipForwardButtonVisibility();
        long nextSkipCountUpdateInMilis = stationPlayer.getNextSkipCountUpdateTimeInMilis();
        if (nextSkipCountUpdateInMilis == StationPlayer.NEVER) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onSkipCountUpdate();
            }
        }, nextSkipCountUpdateInMilis);
    }

    private void updateSkipCountView() {
        boolean hasUnlimitedSkips = stationPlayer.hasUnlimitedSkips();
        Utils.updateViewVisibility(!hasUnlimitedSkips, tvSkipsLeft);
        if (hasUnlimitedSkips) {
            return;
        }
        int skipsLeft = stationPlayer.getNumberOfSkipsLeft();
        if (skipsLeft == 0) {
            tvSkipsLeft.setText("");
        } else {
            tvSkipsLeft.setText(stationPlayer.getNumberOfSkipsLeft() + "");
        }
    }

    private void showNotEnoughSkipsLeftMessage() {
        Toast.makeText(getActivity(), getString(R.string.no_skips), Toast.LENGTH_SHORT).show();
    }

    private void updateLikeButtons(StationTrackLikedState currentTrackLikedState) {
        updateViewHighlighted(vLike, currentTrackLikedState == StationTrackLikedState.LIKED);
        updateViewHighlighted(vDislike, currentTrackLikedState == StationTrackLikedState.DISLIKED);
    }

    private void updateViewHighlighted(View view, boolean highlighted) {
        if(highlighted) {
            view.setBackgroundColor(colorHighlighted);
        } else {
            view.setBackgroundColor(colorNormal);
        }
    }

    private void updateSkipBackwardButtonVisibility() {
        Utils.updateViewVisibility(stationPlayer.canSkipBackward(), btnPrevious);
        btnPrevious.setEnabled(true);
    }

    private void updateSkipForwardButtonVisibility() {
        Utils.updateViewVisibility(stationPlayer.canSkipForward(), btnNext);
    }

    private void updateLikeButtonsVisibility() {
        Utils.updateViewVisibility(stationPlayer.canTune(), vLike, vDislike);
    }

    @Override
    public void onStationPlayerCreationSuccess(StationPlayer stationPlayer) {
        this.stationPlayer = stationPlayer;
        stationPlayer.setPreviewTracksChangeListener(this);

        StationPlayerSampleApplication app = (StationPlayerSampleApplication) getActivity().getApplication();
        app.setStationPlayer(stationPlayer);

        miniPlayerContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStationPlayerCreationError(NapsterError error) {
        Log.d("SDK", error.toString());
        if(isLoginError(error)) {
            onLoginError();
        } else {
            if(getActivity() != null) {
                Toast.makeText(getActivity(), R.string.station_player_error, Toast.LENGTH_SHORT).show();
            }
            //you can retry...
        }
    }

    CompletionCallback tuningCompleteCallback = new CompletionCallback() {
        @Override
        public void onSuccess() {
            updateLikeButtons(stationPlayer.getCurrentTrackLikedState());
        }

        @Override
        public void onError(NapsterError error) {
            //maybe retry
            updateLikeButtons(stationPlayer.getCurrentTrackLikedState());
        }
    };

}
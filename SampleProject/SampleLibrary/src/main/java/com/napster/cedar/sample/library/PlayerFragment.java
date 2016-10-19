package com.napster.cedar.sample.library;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.napster.cedar.NapsterError;
import com.napster.cedar.player.PlaybackState;
import com.napster.cedar.player.PlayerStateListener;
import com.napster.cedar.player.Player;
import com.napster.cedar.player.data.Track;
import com.napster.cedar.sample.library.metadata.Metadata;

public abstract class PlayerFragment extends Fragment implements PlayerStateListener, View.OnClickListener {

    protected static final String LOADING = "...";
    final static int PROGRESS_SMOOTHING_FACTOR = 100;
    final static int UPDATE_PROGRESS_INTERVAL_IN_MILIS = 1000 / PROGRESS_SMOOTHING_FACTOR;

    protected boolean isSeeking;
    protected boolean updateSeekBar;

    protected SeekBar seekBar;
    protected View btnStop;
    protected View btnPlay;
    protected View btnPause;
    protected View btnNext;
    protected View btnPrevious;
    protected TextView tvTrackName;
    protected TextView tvTrackArtist;
    protected ListView tracksListView;
    protected TrackAdapter trackAdapter;

    protected View miniPlayerContainer;

    protected Player player;
    protected Metadata metadata;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NapsterSampleApplication app = (NapsterSampleApplication) getActivity().getApplication();
        player = app.getPlayer();
        metadata = new Metadata(app.getAppInfo().getApiKey());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        setupSeekBar();
        setupTracksAdapter();
    }

    private void setupViews(View view) {
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        btnPlay = view.findViewById(R.id.play);
        btnPlay.setOnClickListener(this);
        btnStop = view.findViewById(R.id.stop);
        btnStop.setOnClickListener(this);
        btnPause = view.findViewById(R.id.pause);
        btnPause.setOnClickListener(this);
        btnNext = view.findViewById(R.id.next_track);
        btnNext.setOnClickListener(this);
        btnPrevious = view.findViewById(R.id.prev_track);
        btnPrevious.setOnClickListener(this);
        tvTrackName = (TextView) view.findViewById(R.id.track_name);
        tvTrackArtist = (TextView) view.findViewById(R.id.track_artist);
        tracksListView = (ListView) view.findViewById(R.id.tracks);
        miniPlayerContainer = view.findViewById(R.id.player);
    }

    @Override
    public void onResume() {
        super.onResume();
        player.addStateListener(this);
        onStateChange(player.getPlaybackState());
    }

    @Override
    public void onPause() {
        super.onPause();
        player.removeStateListener(this);
    }

    @Override
    public void onStateChange(PlaybackState state) {
        switch (state) {
            case SEEKING:
                return;
            case PLAYING:
                onPlaybackStarted();
                break;
            case STOPPED:
                onPlaybackStopped();
                updateSeekBar = false;
                break;
            case COMPLETE:
                clearTrackInfo();
                break;
            default:
                break;
        }
        if (!isSeeking) {
            updatePlayPauseButtons(state == PlaybackState.PLAYING);
        }
    }

    protected abstract void onPlaybackStopped();

    protected void onPlaybackStarted() {
        updateTrackInfo();
        seekBar.setMax(player.getCurrentTrackDuration() * PROGRESS_SMOOTHING_FACTOR);
        updateSeekBar = true;

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(player.getPlayheadPosition() * PROGRESS_SMOOTHING_FACTOR);
                if (updateSeekBar) {
                    handler.postDelayed(this, UPDATE_PROGRESS_INTERVAL_IN_MILIS);
                }
            }
        };
        handler.post(runnable);
    }

    protected void updateTrackInfo() {
        updateTrackInfo(player.getCurrentTrack());
    }

    protected void updateTrackInfo(Track track) {
        if(track != null) {
            tvTrackName.setText(track.name);
            tvTrackArtist.setText(track.artistName);
        }
    }

    @Override
    public void onError(NapsterError error) {
        if (error.type == NapsterError.Type.AUTHENTICATION) {
            Toast.makeText(getActivity(), getString(R.string.login_required), Toast.LENGTH_LONG).show();
            getActivity().invalidateOptionsMenu();
        } else if(error.type == NapsterError.Type.TRACK_VALIDATION_FAILED) {
            Toast.makeText(getActivity(), getString(R.string.track_validation_failed), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.playback_error), Toast.LENGTH_LONG).show();
        }
    }

    private void updatePlayPauseButtons(boolean isPlaying) {
        if (isPlaying) {
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        } else {
            btnPause.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
        }
    }

    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private boolean playAfterSeek;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                player.seek(seekBar.getProgress() / PROGRESS_SMOOTHING_FACTOR, playAfterSeek);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
                playAfterSeek = player.getPlaybackState() == PlaybackState.PLAYING;
                updateSeekBar = false;
                player.pause();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
    }

    private void setupTracksAdapter() {
        trackAdapter = new TrackAdapter(getActivity());
        tracksListView.setAdapter(trackAdapter);
    }

    @Override
    public void onClick(View view) {
       int id = view.getId();
        if (id == R.id.play) {
            play();
            updateTrackInfo();
        } else if(id == R.id.stop) {
            stop();
        } else if(id == R.id.pause) {
            pause();
        } else if(id == R.id.next_track) {
            playNextTrack();
            updateTrackInfo();
        } else if(id == R.id.prev_track) {
            playPreviousTrack();
            updateTrackInfo();
        }
    }

    protected void clearTrackInfo() {
        tvTrackName.setText(LOADING);
        tvTrackArtist.setText("");
    }

    protected abstract void play();

    protected abstract void stop();

    protected abstract void pause();

    protected abstract void playNextTrack();

    protected abstract void playPreviousTrack();

}

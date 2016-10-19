package com.napster.cedar.sample.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.napster.cedar.AuthorizedRequest;
import com.napster.cedar.Napster;
import com.napster.cedar.NapsterError;
import com.napster.cedar.player.PlaybackState;
import com.napster.cedar.sample.R;
import com.napster.cedar.sample.library.AppInfo;
import com.napster.cedar.sample.library.PlayerFragment;
import com.napster.cedar.sample.library.Utils;
import com.napster.cedar.sample.library.metadata.Tracks;
import com.napster.cedar.sample.library.tracklistplayer.RepeatMode;
import com.napster.cedar.sample.library.tracklistplayer.TrackList;
import com.napster.cedar.sample.library.tracklistplayer.TrackListPlayer;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopTenFragment extends PlayerFragment  implements TrackListPlayer.SequenceChangeListener, AdapterView.OnItemLongClickListener {

	static final int TRACK_COUNT = 10;
    TrackListPlayer trackListPlayer;

    View vShuffle;
    ImageButton ibRepeat;
    int colorNormal, colorHighlighted;

    Drawable iconRepeat;
    Drawable iconRepeatSingle;

    AppInfo appInfo;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TopTenSampleApplication app = (TopTenSampleApplication) getActivity().getApplication();
        trackListPlayer = app.getTrackListPlayer();
        appInfo = app.getAppInfo();
        setHasOptionsMenu(true);

        Resources res = getResources();
        colorHighlighted = res.getColor(R.color.accent_light);
        colorNormal = res.getColor(android.R.color.transparent);

        iconRepeat = res.getDrawable(R.drawable.repeat);
        iconRepeatSingle = res.getDrawable(R.drawable.repeat_one);

        setHasOptionsMenu(true);
	}

    @Override
    public void onResume() {
        super.onResume();
        trackListPlayer.setOnTrackChangeListener(this);
    }

    @Override
    public void onPause() {
        trackListPlayer.setOnTrackChangeListener(TrackListPlayer.SequenceChangeListener.EMPTY);
        super.onPause();
    }

    @Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);


        LinearLayout playerButtonRow = (LinearLayout) view.findViewById(R.id.mini_player_button_row);
        appendAdditionalViews(playerButtonRow);

        tracksListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                playTrack(pos);
                updateTrackInfo();
            }
        });
        loadTopTracks();

        ibRepeat.setOnClickListener(this);
        vShuffle.setOnClickListener(this);

        tracksListView.setOnItemLongClickListener(this);
	}

    private void appendAdditionalViews(LinearLayout root) {
        Activity activity = getActivity();
        vShuffle = activity.getLayoutInflater().inflate(R.layout.btn_shuffle, root, false);
        root.addView(vShuffle, 0);
        View btnRepeat = activity.getLayoutInflater().inflate(R.layout.btn_repeat, root, false);
        root.addView(btnRepeat, root.getChildCount());
        ibRepeat = (ImageButton) btnRepeat.findViewById(R.id.repeat);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_load_tracks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_listening_history) {
            player.stop();
            loadListeningHistory();
            return true;
        } else if(id == R.id.menu_item_top_tracks) {
            player.stop();
            loadTopTracks();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == ibRepeat) {
            trackListPlayer.setRepeatMode(getNextRepeatMode(trackListPlayer.getRepeatMode()));
        } else if(view == vShuffle) {
            trackListPlayer.toggleShuffleEnabled();
        }
    }

    @Override
    protected void onPlaybackStopped() {
    }

    @Override
    protected void play() {
        if (player.getCurrentTrack() == null || player.getPlaybackState() == PlaybackState.STOPPED) {
            trackListPlayer.play();
        } else {
            player.resume();
        }
        updateTrackInfo();
    }

    @Override
    protected void stop() {
        player.stop();
    }

    @Override
    protected void pause() {
        player.pause();
    }

    private void loadTopTracks() {
		metadata.getTopTracks(TRACK_COUNT, 0, new Callback<Tracks>() {

			@Override
			public void success(Tracks tracks, Response response) {
                trackAdapter.updateTracks(tracks.tracks);
                trackListPlayer.setTrackList(new TrackList(tracks.tracks));
			}

			@Override
			public void failure(RetrofitError error) {
			}
		});
	}

    private void loadListeningHistory() {

        new AuthorizedRequest<Tracks>(Napster.getInstance().getSessionManager()) {
            @Override
            protected void onSessionValid() {
                metadata.getTrackService().getListeningHistory(getAuthorizationBearer(), this);
            }

            @Override
            protected void onError(NapsterError napsterError, RetrofitError retrofitError) {
                Toast.makeText(getActivity(), R.string.login_required, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(Tracks tracks, Response response) {
                trackAdapter.updateTracks(tracks.tracks);
                trackListPlayer.setTrackList(new TrackList(tracks.tracks));
            }
        }.execute();
    }

	private void playTrack(int trackIndex) {
        trackListPlayer.playTrackAtIndex(trackIndex);
	}

    @Override
	protected void playNextTrack() {
        trackListPlayer.playNextTrack();
	}

    protected void playPreviousTrack() {
        trackListPlayer.playPreviousTrack();
	}

    @Override
    public void onSequenceChanged() {
        Utils.updateViewVisibility(trackListPlayer.canSkipBackward(), btnPrevious);
        Utils.updateViewVisibility(trackListPlayer.canSkipForward(), btnNext);

        RepeatMode currentRepeatMode = trackListPlayer.getRepeatMode();

        updateRepeatIcon(currentRepeatMode);
        updateViewHighlighted(ibRepeat, isRepeating(currentRepeatMode));
        updateViewHighlighted(vShuffle, trackListPlayer.isShuffleEnabled());

        int currentTrackIndex = trackListPlayer.getCurrentTrackIndex();
        trackAdapter.setCurrentItem(currentTrackIndex);
        tracksListView.smoothScrollToPosition(currentTrackIndex);
    }

    private void updateRepeatIcon(RepeatMode currentRepeatMode) {
        if(currentRepeatMode == RepeatMode.Single) {
            ibRepeat.setImageDrawable(iconRepeatSingle);
        } else {
            ibRepeat.setImageDrawable(iconRepeat);
        }
    }

    private boolean isRepeating(RepeatMode repeatMode) {
        return repeatMode == RepeatMode.Single || repeatMode == RepeatMode.All;
    }

    private RepeatMode getNextRepeatMode(RepeatMode currentRepeatMode) {
        if (currentRepeatMode == RepeatMode.None) {
            return RepeatMode.All;
        }
        if (currentRepeatMode == RepeatMode.All) {
            return RepeatMode.Single;
        }
        return RepeatMode.None;
    }

    private void updateViewHighlighted(View view, boolean highlighted) {
        if(highlighted) {
            view.setBackgroundColor(colorHighlighted);
        } else {
            view.setBackgroundColor(colorNormal);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int pos, long l) {
        AlertDialog editTrackDialog = new AlertDialog.Builder(getActivity())
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean shouldSkip = pos == trackListPlayer.getCurrentTrackIndex();
                        trackListPlayer.getTrackList().remove(pos);
                        trackAdapter.updateTracks(trackListPlayer.getTrackList().getTracks());
                        if(shouldSkip && trackListPlayer.canSkipForward()) {
                            trackListPlayer.playNextTrack();
                        } else if(shouldSkip) {
                            trackListPlayer.play();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.duplicate), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TrackList trackList = trackListPlayer.getTrackList();
                        trackList.add(trackList.getTracks().get(pos));
                        trackAdapter.updateTracks(trackListPlayer.getTrackList().getTracks());
                    }
                })
                .create();
        editTrackDialog.show();
        return true;
    }

}

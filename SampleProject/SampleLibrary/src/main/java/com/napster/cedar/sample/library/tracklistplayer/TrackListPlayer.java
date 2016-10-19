package com.napster.cedar.sample.library.tracklistplayer;

import android.util.Log;

import com.napster.cedar.NapsterError;
import com.napster.cedar.player.PlaybackState;
import com.napster.cedar.player.Player;
import com.napster.cedar.player.PlayerStateListener;
import com.napster.cedar.player.data.Track;
import com.napster.cedar.sample.library.tracklistplayer.sequence.NaturalTrackSequencer;
import com.napster.cedar.sample.library.tracklistplayer.sequence.SequenceItem;
import com.napster.cedar.sample.library.tracklistplayer.sequence.ShuffleTrackSequencer;
import com.napster.cedar.sample.library.tracklistplayer.sequence.TrackSequencer;

import java.util.List;

public class TrackListPlayer implements PlayerStateListener{

    protected Player player;
    protected RepeatMode repeatMode = RepeatMode.None;
    protected SequenceChangeListener sequenceChangeListener = SequenceChangeListener.EMPTY;
    protected TrackSequencer trackSequencer;
    protected boolean isShuffleEnabled = false;
    protected TrackList trackList;

    public TrackListPlayer(Player player) {
        this.player = player;
        player.addStateListener(this);
    }

    public void setOnTrackChangeListener(SequenceChangeListener sequenceChangeListener) {
        this.sequenceChangeListener = sequenceChangeListener;
    }

    public void play() {
        if(trackSequencer == null) {
            Log.e(this.getClass().getSimpleName(), "Sequence not set!");
            return;
        }
        trackSequencer.setupInitialTrack();
        playCurrentTrack();
    }

    protected void playCurrentTrack() {
        SequenceItem currentTrack = trackSequencer.getCurrentTrack();
        if(currentTrack == null) {
            Log.e(this.getClass().getSimpleName(), "No current track!");
        } else {
            play(currentTrack.track);
            sequenceChangeListener.onSequenceChanged();
        }
    }

    protected void play(Track track) {
        player.play(track);
    }

    public void playTrackAtIndex(int index) {
        trackSequencer.setCurrentTrack(index);
        playCurrentTrack();
    }

    public boolean canSkipForward() {
        return trackSequencer.canSkipForward(repeatMode);
    }

    public void playNextTrack() {
        if(canSkipForward()) {
            repeatMode.skipForward(trackSequencer);
            playCurrentTrack();
        }
    }

    public boolean canSkipBackward() {
        return trackSequencer.canSkipBackward(repeatMode);
    }

    public void playPreviousTrack() {
        if(canSkipBackward()) {
            repeatMode.skipBackward(trackSequencer);
            playCurrentTrack();
        }
    }

    public void setRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
        sequenceChangeListener.onSequenceChanged();
    }

    public void setTrackList(TrackList trackList) {
        this.trackList = trackList;
        trackSequencer = createTrackSequence();
        trackList.setChangeListener(new TrackListChangeListener());
        sequenceChangeListener.onSequenceChanged();
    }

    public int getCurrentTrackIndex() {
        return trackSequencer.getCurrentTrackIndex();
    }

    public boolean isShuffleEnabled() {
        return isShuffleEnabled;
    }

    public void toggleShuffleEnabled() {
        setShuffleEnabled(!isShuffleEnabled);
    }

    public void setShuffleEnabled(boolean isShuffleEnabled) {
        if(this.isShuffleEnabled != isShuffleEnabled) {
            int currentIndex = getCurrentTrackIndex();
            this.isShuffleEnabled = isShuffleEnabled;
            trackSequencer = createTrackSequence();
            trackSequencer.setCurrentTrack(currentIndex);
            sequenceChangeListener.onSequenceChanged();
        }
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    @Override
    public void onStateChange(PlaybackState playbackState) {
        switch (playbackState) {
            case COMPLETE:
                playNextTrack();
        }
    }

    @Override
    public void onError(NapsterError napsterError) {
    }

    public TrackList getTrackList() {
        return trackList;
    }

    public interface SequenceChangeListener {
        void onSequenceChanged();

        public static final SequenceChangeListener EMPTY = new SequenceChangeListener() {
            @Override
            public void onSequenceChanged() {
                //NOTHING
            }
        };
    }

    private TrackSequencer createTrackSequence() {
        if(isShuffleEnabled) {
            return new ShuffleTrackSequencer(trackList);
        } else {
            return new NaturalTrackSequencer(trackList);
        }
    }

    private class TrackListChangeListener implements TrackList.ChangeListener {
        @Override
        public void onAdd(SequenceItem track) {
            trackSequencer.onAdd(track);
            sequenceChangeListener.onSequenceChanged();
        }

        @Override
        public void onAdd(List<SequenceItem> tracks) {
            trackSequencer.onAdd(tracks);
            sequenceChangeListener.onSequenceChanged();
        }

        @Override
        public void onRemove(int index, SequenceItem removedItem) {
            trackSequencer.onRemove(index, removedItem);
            sequenceChangeListener.onSequenceChanged();
        }
    }
}

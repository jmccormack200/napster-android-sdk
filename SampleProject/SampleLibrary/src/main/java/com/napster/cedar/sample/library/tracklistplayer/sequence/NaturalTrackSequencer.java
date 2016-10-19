package com.napster.cedar.sample.library.tracklistplayer.sequence;

import com.napster.cedar.sample.library.tracklistplayer.RepeatMode;
import com.napster.cedar.sample.library.tracklistplayer.TrackList;

import java.util.List;

public class NaturalTrackSequencer implements TrackSequencer {

    //most recent goes at front
    int currentTrackIndex = 0;
    TrackList trackList;

    public NaturalTrackSequencer(TrackList trackList) {
        this.trackList = trackList;
    }

    @Override
    public boolean canSkipBackward(RepeatMode repeatMode) {
        return canSkipBackward() || repeatMode.canAlwaysSkip();
    }

    @Override
    public boolean canSkipForward(RepeatMode repeatMode) {
        return canSkipForward() || repeatMode.canAlwaysSkip();
    }

    @Override
    public boolean canSkipBackward() {
        return currentTrackIndex > 0;
    }

    @Override
    public boolean canSkipForward() {
        return currentTrackIndex < getLastTrackIndex();
    }

    @Override
    public void skipBackward() {
        currentTrackIndex--;
    }

    @Override
    public void skipForward() {
        currentTrackIndex++;
    }

    @Override
    public void skipToFirst() {
        currentTrackIndex = 0;
    }

    @Override
    public void skipToLast() {
        currentTrackIndex = getLastTrackIndex();
    }

    @Override
    public void setCurrentTrack(int index) {
        currentTrackIndex = index;
    }

    @Override
    public SequenceItem getCurrentTrack() {
        return trackList.getSequence().get(currentTrackIndex);
    }

    @Override
    public int getCurrentTrackIndex() {
        return currentTrackIndex;
    }

    @Override
    public void setupInitialTrack() {
        currentTrackIndex = 0;
    }

    private int getLastTrackIndex() {
        return trackList.count() - 1;
    }

    @Override
    public void onAdd(SequenceItem track) {
    }

    @Override
    public void onAdd(List<SequenceItem> tracks) {
    }

    @Override
    public void onRemove(int index, SequenceItem removedItem) {
        if(index <= currentTrackIndex) {
            currentTrackIndex--;
        }
    }
}

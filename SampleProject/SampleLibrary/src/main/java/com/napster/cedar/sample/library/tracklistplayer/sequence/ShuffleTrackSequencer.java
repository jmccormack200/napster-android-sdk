package com.napster.cedar.sample.library.tracklistplayer.sequence;

import android.util.Log;

import com.napster.cedar.sample.library.tracklistplayer.RepeatMode;
import com.napster.cedar.sample.library.tracklistplayer.TrackList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShuffleTrackSequencer implements TrackSequencer {

    TrackList trackList;
    TrackHistory trackHistory = new TrackHistory();
    List<SequenceItem> tracksNotPlayed = new ArrayList<SequenceItem>();
    SequenceItem currentTrack;

    Random random = new Random();

    public ShuffleTrackSequencer(TrackList trackList) {
        this.trackList = trackList;
        tracksNotPlayed.addAll(trackList.getSequence());
    }

    @Override
    public boolean canSkipBackward(RepeatMode repeatMode) {
        return canSkipBackward();
    }

    @Override
    public boolean canSkipForward(RepeatMode repeatMode) {
        return canSkipForward() || repeatMode.canAlwaysSkip();
    }

    @Override
    public boolean canSkipBackward() {
        return trackHistory.canSkipBackwards();
    }

    @Override
    public boolean canSkipForward() {
        return !tracksNotPlayed.isEmpty() || trackHistory.canSkipForward();
    }

    @Override
    public void skipBackward() {
        if(!trackHistory.isPlayingHistoryTrack()) {
            trackHistory.addTrackOnSkipBackward(currentTrack);
        }
        currentTrack = trackHistory.getPreviousTrack();
    }

    @Override
    public void skipForward() {
        if(trackHistory.canSkipForward()) {
            currentTrack = trackHistory.getNextTrack();
        } else {
            trackHistory.addTrackOnSkipForward(currentTrack);
            currentTrack = popRandomNotPlayedTrack();
        }
    }

    @Override
    public void skipToFirst() {
        tracksNotPlayed.clear();
        for (SequenceItem track : trackList.getSequence()) {
            tracksNotPlayed.add(track);
        }
        currentTrack = popRandomNotPlayedTrack();
    }

    @Override
    public void skipToLast() {
        //can't do this
    }

    @Override
    public void setCurrentTrack(int index) {
        SequenceItem track = trackList.getSequence().get(index);

        if (track == currentTrack) {
            return;
        }

        trackHistory.addTrackOnSetCurrentTrack(currentTrack);
        if(trackHistory.canSkipToTrack(track)) {
            currentTrack = trackHistory.skipToTrack(track);
        } else {
            int indexInNotPlayed = tracksNotPlayed.indexOf(track);
            currentTrack = popNotPlayedTrack(indexInNotPlayed);
        }
    }

    @Override
    public SequenceItem getCurrentTrack() {
        Log.e("PLAYED", trackHistory.toString());
        Log.e("CURRENT", currentTrack.toString());
        Log.e("PAUSE", "-------------------");
        return currentTrack;
    }

    @Override
    public int getCurrentTrackIndex() {
        return trackList.getSequence().indexOf(currentTrack);
    }

    @Override
    public void setupInitialTrack() {
        currentTrack = popRandomNotPlayedTrack();
    }

    private SequenceItem popNotPlayedTrack(int index) {
        SequenceItem track = tracksNotPlayed.get(index);
        tracksNotPlayed.remove(index);
        return track;
    }

    private SequenceItem popRandomNotPlayedTrack() {
        int index = random.nextInt(tracksNotPlayed.size());
        return popNotPlayedTrack(index);
    }

    @Override
    public void onAdd(SequenceItem track) {
        tracksNotPlayed.add(track);
    }

    @Override
    public void onAdd(List<SequenceItem> tracks) {
        tracksNotPlayed.addAll(tracks);
    }

    @Override
    public void onRemove(int index, SequenceItem removedItem) {
        if(removedItem == currentTrack) {
            currentTrack = null;
        } else {
            tracksNotPlayed.remove(removedItem);
            trackHistory.remove(removedItem);
        }
    }
}
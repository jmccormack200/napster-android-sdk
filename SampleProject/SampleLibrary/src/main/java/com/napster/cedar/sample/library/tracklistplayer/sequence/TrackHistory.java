package com.napster.cedar.sample.library.tracklistplayer.sequence;

import com.napster.cedar.sample.library.Utils;

import java.util.ArrayList;
import java.util.List;

public class TrackHistory {

    private static final int STARTING_INDEX = -1;
    int currentTrackIndex = STARTING_INDEX;
    List<SequenceItem> tracks = new ArrayList<SequenceItem>();

    public boolean canSkipBackwards() {
        return !tracks.isEmpty() && currentTrackIndex + 1 < tracks.size();
    }

    public boolean canSkipForward() {
        return currentTrackIndex > 0;
    }

    public boolean canSkipToTrack(SequenceItem track) {
        return isValidTrackIndex(tracks.indexOf(track));
    }

    public SequenceItem skipToTrack(SequenceItem track) {
        currentTrackIndex = tracks.indexOf(track);
        return tracks.get(currentTrackIndex);
    }

    public boolean isPlayingHistoryTrack() {
        return currentTrackIndex > STARTING_INDEX;
    }

    public void addTrackOnSkipBackward(SequenceItem track) {
        addTrack(track);
        currentTrackIndex++;
    }

    public void addTrackOnSkipForward(SequenceItem track) {
        addTrack(track);
        currentTrackIndex = Utils.truncate(currentTrackIndex - 1, STARTING_INDEX, tracks.size());
    }

    public void addTrackOnSetCurrentTrack(SequenceItem track) {
        addTrack(track);
        currentTrackIndex = STARTING_INDEX;
    }

    private void addTrack(SequenceItem track) {
        if (track == null) {
            return;
        }
        int index = tracks.indexOf(track);
        if (isValidTrackIndex(index)) {
            tracks.remove(index);
        }
        tracks.add(0, track);
    }

    public SequenceItem getPreviousTrack() {
        return tracks.get(++currentTrackIndex);
    }

    public SequenceItem getNextTrack() {
        return tracks.get(--currentTrackIndex);
    }

    public void remove(SequenceItem trackToRemove) {
        int index = tracks.indexOf(trackToRemove);
        if (!isValidTrackIndex(index)) {
            return;
        }

        tracks.remove(index);
        if (currentTrackIndex <= index) {
            currentTrackIndex--;
        }
    }

    private boolean isValidTrackIndex(int index) {
        return index >= 0 && index < tracks.size();
    }

    @Override
    public String toString() {
        return tracks.toString();
    }

}

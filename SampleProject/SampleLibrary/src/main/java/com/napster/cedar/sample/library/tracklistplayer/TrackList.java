package com.napster.cedar.sample.library.tracklistplayer;

import com.napster.cedar.player.data.Track;
import com.napster.cedar.sample.library.tracklistplayer.sequence.SequenceItem;

import java.util.ArrayList;
import java.util.List;

public class TrackList {

    private List<SequenceItem> sequence = new ArrayList<SequenceItem>();
    private ChangeListener changeListener = ChangeListener.EMPTY;

    public TrackList(List<Track> sequence) {
        for(Track track : sequence) {
            this.sequence.add(new SequenceItem(track));
        }
    }

    public void setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public List<SequenceItem> getSequence() {
        return sequence;
    }

    public List<Track> getTracks() {
        List<Track> tracks = new ArrayList<Track>();
        for(SequenceItem sequenceItem : sequence) {
            tracks.add(sequenceItem.track);
        }
        return tracks;
    }

    public int count() {
        return sequence.size();
    }

    public void add(Track track) {
        SequenceItem newTrack = new SequenceItem(track);
        this.sequence.add(newTrack);
        changeListener.onAdd(newTrack);
    }

    public void add(List<Track> tracks) {
        List<SequenceItem> sequence = new ArrayList<SequenceItem>();
        for(Track track : tracks) {
            sequence.add(new SequenceItem(track));
        }
        this.sequence.addAll(sequence);
        changeListener.onAdd(sequence);
    }

    public void remove(int index) {
        SequenceItem trackToRemove = sequence.get(index);
        this.sequence.remove(trackToRemove);
        changeListener.onRemove(index, trackToRemove);
    }

    public interface ChangeListener {
        void onAdd(SequenceItem track);
        void onAdd(List<SequenceItem> tracks);
        void onRemove(int index, SequenceItem removedItem);

        static ChangeListener EMPTY = new ChangeListener() {
            @Override
            public void onAdd(SequenceItem track) {
            }

            @Override
            public void onAdd(List<SequenceItem> tracks) {
            }

            @Override
            public void onRemove(int index, SequenceItem removedItem) {
            }
        };
    }



}

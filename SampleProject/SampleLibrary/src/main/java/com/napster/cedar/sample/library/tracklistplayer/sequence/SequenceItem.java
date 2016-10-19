package com.napster.cedar.sample.library.tracklistplayer.sequence;

import com.napster.cedar.player.data.Track;

import java.util.UUID;

public class SequenceItem {

    public final Track track;
    private final String id;

    public SequenceItem(Track track) {
        this.track = track;
        id = UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof SequenceItem)) {
            return false;
        }
        return ((SequenceItem) other).id.equals(this.id);
    }

    @Override
    public String toString() {
        return track.toString();
    }
}

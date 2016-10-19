package com.napster.cedar.sample.library.tracklistplayer.sequence;

import com.napster.cedar.sample.library.tracklistplayer.RepeatMode;
import com.napster.cedar.sample.library.tracklistplayer.TrackList;

public interface TrackSequencer extends TrackList.ChangeListener{

    boolean canSkipBackward(RepeatMode repeatMode);
    boolean canSkipForward(RepeatMode repeatMode);
    boolean canSkipBackward();
    boolean canSkipForward();

    void skipBackward();
    void skipForward();

    void skipToFirst();
    void skipToLast();

    void setCurrentTrack(int index);
    SequenceItem getCurrentTrack();
    int getCurrentTrackIndex();

    void setupInitialTrack();
}

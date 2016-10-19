package com.napster.cedar.sample.library.tracklistplayer;

import com.napster.cedar.sample.library.tracklistplayer.sequence.TrackSequencer;

public interface RepeatMode {

    void skipForward(TrackSequencer sequence);
    void skipBackward(TrackSequencer sequence);
    boolean canAlwaysSkip();

    public static final RepeatMode None = new RepeatMode() {
        @Override
        public void skipForward(TrackSequencer sequence) {
            sequence.skipForward();
        }

        @Override
        public void skipBackward(TrackSequencer sequence) {
            sequence.skipBackward();
        }

        @Override
        public boolean canAlwaysSkip() {
            return false;
        }
    };


    public static final RepeatMode Single = new RepeatMode() {
        @Override
        public void skipForward(TrackSequencer sequence) {
            //NOTHING
        }

        @Override
        public void skipBackward(TrackSequencer sequence) {
            //NOTHING
        }

        @Override
        public boolean canAlwaysSkip() {
            return true;
        }
    };


    public static final RepeatMode All = new RepeatMode() {
        @Override
        public void skipForward(TrackSequencer sequence) {
            if(sequence.canSkipForward()) {
                sequence.skipForward();
            } else {
                sequence.skipToFirst();
            }
        }

        @Override
        public void skipBackward(TrackSequencer sequence) {
            if(sequence.canSkipBackward()) {
                sequence.skipBackward();
            } else {
                sequence.skipToLast();
            }
        }

        @Override
        public boolean canAlwaysSkip() {
            return true;
        }
    };

}

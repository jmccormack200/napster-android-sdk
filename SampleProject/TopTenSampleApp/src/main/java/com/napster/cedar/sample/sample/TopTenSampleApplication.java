package com.napster.cedar.sample.sample;

import android.content.Intent;

import com.napster.cedar.player.notification.NotificationAction;
import com.napster.cedar.sample.library.AppInfo;
import com.napster.cedar.sample.library.NapsterSampleApplication;
import com.napster.cedar.sample.library.tracklistplayer.TrackListPlayer;

public class TopTenSampleApplication extends NapsterSampleApplication {

    private TrackListPlayer trackListPlayer;

	@Override
	public void onCreate() {
		super.onCreate();
        trackListPlayer = new TrackListPlayer(player);
	}

    public TrackListPlayer getTrackListPlayer() {
        return trackListPlayer;
    }

	@Override
	public void onNotificationAction(NotificationAction action) {
        switch (action) {
            case OPEN_PLAYER:
                Intent i = new Intent(this, TopTenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
            case NEXT:
                if(trackListPlayer.canSkipForward()) {
                    trackListPlayer.playNextTrack();
                }
                break;
            case PREVIOUS:
                if(trackListPlayer.canSkipBackward()) {
                    trackListPlayer.playPreviousTrack();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public AppInfo getAppInfo() {
        return topTenSampleAppInfo;
    }

    private AppInfo topTenSampleAppInfo = new AppInfo() {
        @Override
        public String getApiKey() {
            return "NjQzYThjNTEtNmUwOS00NjA1LTg1Y2MtMzkwNTcyZWIyZjk4";
        }

        @Override
        public String getSecret() {
            return "YjE3MGNmZWItZDU3NS00ZjJiLWIwYmMtZjliOTBlNmU4ODBm";
        }

        @Override
        public String getRedirectUrl() {
            return "sample://authorize";
        }
    };

}

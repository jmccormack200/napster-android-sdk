package com.napster.cedar.sample.sample;

import android.content.Intent;

import com.napster.cedar.player.notification.NotificationAction;
import com.napster.cedar.sample.library.AppInfo;
import com.napster.cedar.sample.library.NapsterSampleApplication;
import com.napster.cedar.sample.sample.list.StationListActivity;
import com.napster.cedar.station.StationPlayer;

public class StationPlayerSampleApplication extends NapsterSampleApplication {

    StationPlayer stationPlayer;

    public StationPlayer getStationPlayer() {
        return stationPlayer;
    }

    public void setStationPlayer(StationPlayer newStationPlayer) {
        boolean isStationPlayerSet = stationPlayer != null;
        if(isStationPlayerSet && isTheSameStationPlayer(stationPlayer, newStationPlayer)) {
            return;
        }
        if(isStationPlayerSet) {
            stationPlayer.getPlayer().stop();
        }
        stationPlayer = newStationPlayer;
    }

    private boolean isTheSameStationPlayer(StationPlayer first, StationPlayer second) {
        return first.getStationId() == second.getStationId();
    }

    @Override
    public void onNotificationAction(NotificationAction action) {
        switch (action) {
            case OPEN_PLAYER:
                Intent i = new Intent(this, StationListActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
            case NEXT:
                stationPlayer.skipForward();
                break;
            case PREVIOUS:
                stationPlayer.skipBackward();
                break;
            default:
                break;
        }
    }

    @Override
    public AppInfo getAppInfo() {
        return stationPlayerAppInfo;
    }

    private AppInfo stationPlayerAppInfo = new AppInfo() {
        @Override
        public String getApiKey() {
            return "ZTU5NmEwY2ItN2ZiZi00ODc5LWFhNzUtZjJhOWUwODQ2YmFm";
        }

        @Override
        public String getSecret() {
            return "ZTk0NjJjY2UtYWY2MS00ODhiLTgzYzAtNTY2MDgxOGI5YTRk";
        }

        @Override
        public String getRedirectUrl() {
            return "sample://authorize";
        }
    };

}

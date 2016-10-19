package com.napster.cedar.sample.library;

import android.app.Application;

import com.napster.cedar.Napster;
import com.napster.cedar.player.Player;
import com.napster.cedar.player.notification.NotificationActionListener;
import com.napster.cedar.session.SessionManager;

public abstract class NapsterSampleApplication extends Application implements NotificationActionListener {

	protected Napster napster;
    protected Player player;
    protected SessionManager sessionManager;

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			napster = Napster.register(this, getAppInfo().getApiKey(), getAppInfo().getSecret());
		} catch (IllegalStateException e) {
            return;
		}
		player = napster.getPlayer();
		player.setNotificationProperties(new NotificationProperties());
		player.registerNotificationActionListener(this);
        sessionManager = napster.getSessionManager();
	}

	public Napster getNapster() {
		return napster;
	}

    public Player getPlayer() {
        return player;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public abstract AppInfo getAppInfo();

}

package com.napster.cedar.sample.library;

import android.os.Parcel;

import com.napster.cedar.player.notification.AbstractNotificationProperties;

public class NotificationProperties extends AbstractNotificationProperties {

	public static final Creator<NotificationProperties> CREATOR = new Creator<NotificationProperties>() {
		public NotificationProperties createFromParcel(Parcel in) {
			return new NotificationProperties(in);
		}

		public NotificationProperties[] newArray(int size) {
			return new NotificationProperties[size];
		}
	};

	public NotificationProperties(Parcel in) {
	}

	public NotificationProperties() {
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
	}

	@Override
	public int getAlbumNameTextViewId() {
		return R.id.notif_album_name;
	}

	@Override
	public int getArtistNameTextViewId() {
		return R.id.notif_artist_name;
	}

	@Override
	public int getButtonCloseViewId() {
		return R.id.notif_btn_close;
	}

	@Override
	public int getButtonNextViewId() {
		return R.id.notif_btn_next;
	}

	@Override
	public int getButtonPlayPauseViewId() {
		return R.id.notif_btn_play_pause;
	}

	@Override
	public int getButtonPreviousViewId() {
		return R.id.notif_btn_previous;
	}

	@Override
	public int getDrawableCloseId() {
		return R.drawable.cancel_white;
	}

	@Override
	public int getDrawablePauseId() {
		return R.drawable.pause_white;
	}

	@Override
	public int getDrawablePlayId() {
		return R.drawable.play_white;
	}
	
	@Override
	public int getDrawableNextId() {
		return R.drawable.next_white;
	}
	
	@Override
	public int getDrawablePreviousId() {
		return R.drawable.previous_white;
	}

	@Override
	public int getExpandedLayoutId() {
		return R.layout.notification_expanded;
	}

	@Override
	public int getImageViewId() {
		return R.id.notif_image_view;
	}

	@Override
	public int getLayoutId() {
		return R.layout.notification;
	}

	@Override
	public int getTrackNameTextViewId() {
		return R.id.notif_track_name;
	}

	@Override
	public int getNotificationIcon() {
		return R.drawable.ic_launcher;
	}

}

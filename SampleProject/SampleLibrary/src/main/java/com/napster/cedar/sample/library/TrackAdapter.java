package com.napster.cedar.sample.library;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.napster.cedar.player.data.Track;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends BaseAdapter{

	private List<Track> tracks = new ArrayList<Track>();
	private LayoutInflater inflater;
	private int currentItem = -1;
    int colorHighlighted, colorNormal;

	public TrackAdapter(Context context, List<Track> tracks){
        if(context == null) {
            return;
        }
		this.tracks.addAll(tracks);
        inflater = LayoutInflater.from(context);
        Resources res = context.getResources();
        colorHighlighted = res.getColor(R.color.accent);
        colorNormal = res.getColor(android.R.color.transparent);
	}

    public TrackAdapter(Context context) {
        this(context, new ArrayList<Track>());
    }

    public void updateTracks(List<Track> tracks) {
        this.tracks = tracks;
        this.notifyDataSetChanged();
    }

	@Override
	public int getCount() {
		return tracks.size();
	}

	@Override
	public Track getItem(int pos) {
		return tracks.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return 0;
	}

	@Override
	public View getView(int pos, View view, ViewGroup parent) {
        Track track = getItem(pos);
		view = inflater.inflate(R.layout.list_item_simple, null, false);

		TextView trackTv = (TextView) view.findViewById(R.id.title);
        trackTv.setText(track.name);

		TextView artistTv = (TextView) view.findViewById(R.id.subtitle);
		artistTv.setText(track.artistName);

        if(currentItem == pos) {
            view.setBackgroundColor(colorHighlighted);
        } else {
            view.setBackgroundColor(colorNormal);
        }

		return view;
	}

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
        this.notifyDataSetChanged();
    }

}

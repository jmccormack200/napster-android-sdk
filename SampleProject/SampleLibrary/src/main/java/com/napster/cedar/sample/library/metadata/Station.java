package com.napster.cedar.sample.library.metadata;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Station implements Serializable {

    @SerializedName("id")
    public final String id;

    @SerializedName("name")
    public final String name;

    @SerializedName("summary")
    public final String summary;

    @SerializedName("type")
    public final String type;

    @SerializedName("artists")
    public final String artists;

    @SerializedName("description")
    public final String description;

    @SerializedName("links")
    public final Links links;

    public Station(String id, String name, String type, String artists, String description, String summary, Links links) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.type = type;
        this.artists = artists;
        this.description = description;
        this.links = links;
    }

    public static class Links implements Serializable {

        @SerializedName("mediumImage")
        public final Image mediumImage;

        @SerializedName("largeImage")
        public final Image largeImage;

        public Links(Image mediumImage, Image largeImage) {
            this.mediumImage = mediumImage;
            this.largeImage = largeImage;
        }

    }

    public static class Image implements Serializable {

        @SerializedName("href")
        public final String href;

        public Image(String href) {
            this.href = href;
        }

    }

}

package com.ngoprekweb.popularmovies.data.model;

public class Trailer {
    public static final String SITE_YOUTUBE = "YouTube";

    private String mName;
    private String mSite;
    private String mKey;

    public String getName() {
        return mName;
    }

    public Trailer setName(String name) {
        mName = name;
        return this;
    }

    public String getSite() {
        return mSite;
    }

    public Trailer setSite(String site) {
        mSite = site;
        return this;
    }

    public String getKey() {
        return mKey;
    }

    public Trailer setKey(String key) {
        mKey = key;
        return this;
    }
}

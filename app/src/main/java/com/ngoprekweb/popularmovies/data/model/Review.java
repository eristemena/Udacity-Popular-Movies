package com.ngoprekweb.popularmovies.data.model;

public class Review {
    private String mAuthor;
    private String mContent;

    public String getAuthor() {
        return mAuthor;
    }

    public Review setAuthor(String author) {
        mAuthor = author;
        return this;
    }

    public String getContent() {
        return mContent;
    }

    public Review setContent(String content) {
        mContent = content;
        return this;
    }
}

package com.bignerdranch.android.readingjournal;

import java.util.Date;
import java.util.UUID;

public class Book {
    private UUID mUUID;
    private String mTitle;
    private String mSummary;
    private Date mDate;


    private String mFriend;
    private boolean mIsCompleted;


    public UUID getUUID() {
        return mUUID;
    }

    public void setUUID(UUID UUID) {
        mUUID = UUID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }

    public void setCompleted(boolean completed) {
        mIsCompleted = completed;
    }

    public Book(){
        this(UUID.randomUUID());
    }
    public Book(UUID id){
        mUUID = id;
        mDate = new Date();
    }

    public String getFriend() {
        return mFriend;
    }

    public void setFriend(String friend) {
        mFriend = friend;
    }

    public String getPhotoFileName(){
        return "IMG_"+getUUID().toString() + ".jpg";
    }
}

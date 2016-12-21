package com.tringuyen.anzi.model.event_bus;

/**
 * Created by Tri Nguyen on 12/20/2016.
 */

public class PhotoClickEvent {
    private String mPhotoUrl;

    public PhotoClickEvent(String url)
    {
        this.mPhotoUrl = url;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }
}

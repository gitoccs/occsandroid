package com.occs.ldsoft.occs;

/**
 * Created by yeliu on 15/7/29.
 */
public class TabObject {
    private String title;
    private boolean isOn;
    private String onImage;

    public TabObject(String title, String onImage, boolean isOn) {
        this.title = title;
        this.isOn = isOn;
        this.onImage = onImage;
    }

    public String getTitle() {
        return title;
    }

    public boolean isOn() {
        return isOn;
    }

    public String getOnImage() {
        return onImage;
    }
}

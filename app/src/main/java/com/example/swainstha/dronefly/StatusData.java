package com.example.swainstha.dronefly;

/**
 * Created by swainstha on 5/27/18.
 */

public class StatusData {

    private String title;
    private String value;

    public StatusData(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

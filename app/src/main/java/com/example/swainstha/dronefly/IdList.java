package com.example.swainstha.dronefly;

/**
 * Created by swainstha on 6/1/18.
 */

public class IdList {

    private String id;
    private String value;

    public IdList(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

package com.example.swainstha.dronefly;

/**
 * Created by swainstha on 5/30/18.
 */

public class CheckList {

    private String name;
    private boolean check;

    public CheckList(String name, Boolean check) {
        this.name = name;
        this.check = check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}

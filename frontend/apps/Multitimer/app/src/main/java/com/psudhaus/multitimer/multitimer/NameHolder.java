package com.psudhaus.multitimer.multitimer;

/**
 * Created by psudh on 16-Apr-18.
 */

public class NameHolder {
    private String name;
    private String icon;

    public NameHolder(String name) {
        this.name = name;
    }

    public NameHolder(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }
}

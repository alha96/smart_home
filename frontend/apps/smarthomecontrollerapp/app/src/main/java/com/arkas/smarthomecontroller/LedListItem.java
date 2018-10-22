package com.arkas.smarthomecontroller;

import java.io.Serializable;

/**
 * Created by hatzo on 19.04.2018.
 */

public class LedListItem {

    public String name;
    public String topic;

    public LedListItem(String name, String topic) {
        this.name = name;
        this.topic = topic;
    }

    public String toString(){
        return "Name: " + name + "; Topic: " + topic;
    }

    public String getTopic() {
        return topic;
    }

    public String getName() {
        return name;
    }



}

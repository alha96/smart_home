package com.psudhaus.multitimer.multitimer;

import java.io.Serializable;

/**
 * Created by psudh on 08-Mar-18.
 */

public class TimerHolder implements Serializable{

    private String name;
    private boolean selected;
    private long restTimeMs;
    private long startTimeMs;
    private boolean isDone = false;
    private boolean isCountDown = true;
    private boolean isRunning = false;


    public TimerHolder(){}
    public TimerHolder(long timeSec, String name, boolean selected){
        this.restTimeMs = timeSec * 1000;
        this.name = name;
        this.selected = selected;
    }
    public TimerHolder(TimerHolder old){
        this.name = old.getName();
        this.selected = old.isSelected();
        this.restTimeMs = old.getRestTimeMs();
        this.startTimeMs = old.getStartTimeMs();
        this.isDone = old.isDone();
        this.isCountDown = old.isCountDown();
        this.isRunning = old.isRunning();
    }

    public String getTimeStr() {
        long seconds = restTimeMs / 1000 % 60;
        long minutes = restTimeMs / 1000 / 60 % 60;
        long hours = restTimeMs / 1000 / 60 / 60;
        StringBuilder t = new StringBuilder();
        if (hours > 0 ){
            t.append(hours).append(":");
            if (minutes < 10){
                t.append("0");
            }
        }
        t.append(minutes).append(":");
        if (seconds < 10)
            t.append("0");
        t.append(seconds);
        return t.toString() ;
    }

    public void addSeconds(int seconds){
        restTimeMs += seconds * 1000;
        if (restTimeMs < 0) restTimeMs = 0;
    }
    public void addMinutes(int minutes){
        restTimeMs += minutes * 60 * 1000;
    }

    public long getRestTimeMs() {
        return restTimeMs;
    }
    public long getRestTimeSec() {
        return restTimeMs / 1000;
    }

    public void setRestTimeMs(long restTimeMs) {
        this.restTimeMs = restTimeMs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void secondTick(){
        if (restTimeMs > 0){
            restTimeMs -= 1000;
        } else if (restTimeMs < 0){
            restTimeMs += 1000;
        } else {
            //TODO: Think about what to do if ZERO
        }
    }

    public long getStartTimeMs() {
        return startTimeMs;
    }

    public void setStartTimeMs(long startTimeMs) {
        this.startTimeMs = startTimeMs;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isCountDown() {
        return isCountDown;
    }

    public void setCountDown(boolean countDown) {
        isCountDown = countDown;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String toString(){
        return "Timer: " + name +
                ", isSelected=" +String.valueOf(isSelected())+
                ", restTimeMs="+String.valueOf(restTimeMs);
    }
}

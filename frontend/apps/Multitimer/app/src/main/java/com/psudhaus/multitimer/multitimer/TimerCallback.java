package com.psudhaus.multitimer.multitimer;

/**
 * Created by psudh on 16-Apr-18.
 */

public interface TimerCallback {
    void timerUpdated();
    void timerDoneAlarm(int id);
}

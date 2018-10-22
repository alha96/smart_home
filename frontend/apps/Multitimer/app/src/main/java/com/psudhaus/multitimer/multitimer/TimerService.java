package com.psudhaus.multitimer.multitimer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.json.JSONException;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by psudh on 07-Apr-18.
 */

public class TimerService extends Service {

    List<TimerHolder> timers;

    private static Timer timer = new Timer();
    private TimerCallback callback;

    FutureConnection mqttConnection;

    public void setTimers(List<TimerHolder> timers){
        this.timers = timers;
    }

    private class mainTask extends TimerTask {

        @Override
        public void run() {
            Log.d("Second", "Tick");
            for (int i = 0, timersSize = timers.size(); i < timersSize; i++) {
                TimerHolder t = timers.get(i);
                if (t.isRunning()) {
                    t.secondTick();
                    Log.d("Timer " + t.getName(), t.getTimeStr() + " -- " + t.getRestTimeMs());

                    //if ui view callback exists (ui visible)
                    if (callback != null)
                        callback.timerUpdated();

                    if (t.getRestTimeMs() == 0){
                        //timer just expired
                        t.setDone(true);
                        t.setRunning(false);
                        callback.timerDoneAlarm(i);
                        //TODO callbacks and notification
                    }
                    if (t.isSelected()){
                        try {

                            double percentage = ((1.0 * t.getRestTimeMs()/t.getStartTimeMs()) - 1) * (-1);

                            if (!(percentage < 0 || percentage > 1)){
                                String msg = new JsonBuilder().range(0,percentage).staticObj(0,200,200).build().toString();
                                String msg2 = new JsonBuilder().range(percentage,1).staticObj(0,0,0).build().toString();

                                Future<Void> f3 = mqttConnection.publish("output/kitchen/window/led", msg.getBytes(), QoS.AT_LEAST_ONCE, false);
                                Future<Void> f4 = mqttConnection.publish("output/kitchen/window/led", msg2.getBytes(), QoS.AT_LEAST_ONCE, false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timers = new ArrayList<>();
        timer.scheduleAtFixedRate(new mainTask(), 0, 1000);
        Toast.makeText(this,"service Started", Toast.LENGTH_SHORT).show();

        MQTT mqtt = new MQTT();
        try {
            mqtt.setHost("192.168.178.44", 1883);

            mqttConnection = mqtt.futureConnection();
            Future<Void> f1 = mqttConnection.connect();
            f1.await();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"MQTT connection failed. Check IP  " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
// or
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "Service Binded", Toast.LENGTH_SHORT).show();
        Log.d("Connected to Service", "Binded");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    // Binder given to clients
    private final IBinder mBinder = new ServiceBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class ServiceBinder extends Binder {
        TimerService getService(){
            return TimerService.this;
        }
    }




    public void setCallback(TimerCallback callback){
        this.callback = callback;
    }

    public void createTimer(TimerHolder t){

        timers.add(t);
    }
    public void removeTimer(int id){
        if (id >= 0 && id < timers.size()){
            //timer exists
            Log.d("Remove Timer", "Timer "+id + "removed");
            timers.remove(id);
        }
        //otherwise do nothing
    }
    public void updateTimer(int id, String name, long startTimeMs, long restTimeMs){
        if (id < 0 || id > timers.size() -1)
            return;

        if (name != null)
            timers.get(id).setName(name);
        if (startTimeMs != -1)
            timers.get(id).setStartTimeMs(startTimeMs);
        if (restTimeMs != -1)
            timers.get(id).setRestTimeMs(restTimeMs);
    }

    public void updateAddSeconds(int id, int seconds){
        if (id < 0 || id > timers.size() -1)
            return;


        timers.get(id).addSeconds(seconds);
    }

    public void updateIsRunning(int id, boolean isRunning){
        if (id < 0 || id > timers.size() -1)
            return;

        if (timers.get(id).getRestTimeMs() == 0 && isRunning){
            //count up
        } else {
            timers.get(id).setRunning(isRunning);
        }
    }

    public List<TimerHolder> getTimers(){
        if (timers == null)
            timers = new ArrayList<>();
        return timers;
    }



    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service stopped by system", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}

package com.arkas.smarthomecontroller.mqtt;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.arkas.smarthomecontroller.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttTraceHandler;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by hatzo on 07.04.2018.
 */

public class MqttHelper {

    public static MqttHelper instance = null;
    private static MqttAndroidClient client;
    private static Context applicationContext;

    private MqttHelper() {
    }

    // Make sure you only call this once - from MyApplication.
    public static void init(final Context context) {
        applicationContext = context.getApplicationContext();
    }

    public Context getApplicationContext() {
        if (null == applicationContext) {
            throw new IllegalStateException("Have you called init(context)?");
        }

        return applicationContext;
    }

    public static MqttHelper getInstance() throws MqttException {
        if (instance == null) {
            instance = new MqttHelper();

            Log.i("MqttHelper", "Instance of MqttHelper created");

            String serverURI = getConnectionString();
            Log.i("MqttHelper", "Trying to connect to " + serverURI);
            client = new MqttAndroidClient(applicationContext, serverURI, MqttClient.generateClientId());
            client.connect(getMqttConnectionOption());

            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean b, String s) {
                    Log.i("MqttHelper", "Successfully connected: " + s);
                    Toast.makeText(applicationContext, "Successfully connected to " + s, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void connectionLost(Throwable throwable) {
                    Log.i("MqttHelper", "Connection lost");
                    Toast.makeText(applicationContext, "Connection to MQTT server lost!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    Log.i("MqttHelper", "Message arrived: " + s);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    Log.i("MqttHelper", "Delivery complete.");
                }

            });
        }
        Log.i("MqttHelper", "MqttHelper Instance already existing. Just returning...");
        if(!client.isConnected()){
            Toast.makeText(applicationContext, "Not connected. Check your settings.", Toast.LENGTH_SHORT).show();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    private static String getConnectionString() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String host = sharedPref.getString("pref_ip", applicationContext.getResources().getString(R.string.default_ip));
        String port = sharedPref.getString("pref_port", applicationContext.getResources().getString(R.string.default_port));
        String serverURI = "tcp://" + host + ":" + port;
        return serverURI;
    }


    public void sendMessage(String topic, String message) {
        if (topic == null || message == null) {
            showWarning("Please select a LED first!");
        } else {
            try {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setPayload(message.getBytes());
                client.publish(topic, mqttMessage);
                Log.i("MqttHelper", "Sent message: \n" + message);
            } catch (MqttException e) {
                e.printStackTrace();
                Toast.makeText(applicationContext, R.string.not_connected, Toast.LENGTH_SHORT).show();
                Log.i("MqttHelper", "Not connected to network!");
            }
        }
    }

    private static MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);
        return mqttConnectOptions;
    }

    private static void showWarning(String warning) {
        Toast.makeText(applicationContext, warning, Toast.LENGTH_SHORT).show();
    }

}

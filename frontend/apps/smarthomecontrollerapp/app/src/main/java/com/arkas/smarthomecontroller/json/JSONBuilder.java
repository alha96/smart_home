package com.arkas.smarthomecontroller.json;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by hatzo on 08.04.2018.
 */

public class JSONBuilder {

    private JSONObject wrapper;
    NumberFormat doubleFormatter = new DecimalFormat("#0.00");

    public JSONBuilder(){
        this.wrapper = new JSONObject();
    }

    public JSONBuilder range(double start, double end) {
        JSONObject range = new JSONObject();
        try {
            range.put("start", doubleFormatter.format(start))
                    .put("end", doubleFormatter.format(end));
            wrapper.put("range", range);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JSONBuilder staticObj(int r, int g, int b) {
        JSONObject staticObj = new JSONObject();
        try {
            staticObj.put("r", r)
                    .put("g", g)
                    .put("b", b);
            wrapper.put("static", staticObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JSONBuilder rainbow() {
        try {
            wrapper.put("rainbow", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JSONBuilder kitt(int speed, int length) {
        JSONObject kitt = new JSONObject();
        try {
            kitt.put("speed", speed);
            kitt.put("length", length);
            wrapper.put("kitt", kitt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JSONBuilder blink(int duration) {
        try {
            wrapper.put("blink", duration);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JSONObject build() {
        JSONObject led = new JSONObject();
        try {
            led.put("led", wrapper);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return led;
    }

    private String wrapInApostrophe(String string){
        return "\"" + string + "\"";
    }

    private String wrapInApostrophe(int number){
        return "\'" + String.valueOf(number) + "\'";
    }
}




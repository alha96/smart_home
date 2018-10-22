package com.psudhaus.multitimer.multitimer;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by hatzo on 08.04.2018.
 */

public class JsonBuilder {

    private JSONObject json;
    private JSONObject wrapper;
    private JsonBuilder builder;

    NumberFormat doubleFormatter = new DecimalFormat("#0.00");

    public JsonBuilder(){
        this.wrapper = new JSONObject();
    }

    public JsonBuilder range(double start, double end) throws JSONException {
        JSONObject range = new JSONObject();

        range.put("start", doubleFormatter.format(start))
                .put("end", doubleFormatter.format(end));
        wrapper.put("range", range);
        return this;
    }

    public JsonBuilder staticObj(int r, int g, int b) throws JSONException {
        JSONObject staticObj = new JSONObject();
        staticObj.put("r", r)
                .put("g", g)
                .put("b", b);
        wrapper.put("static", staticObj);
        return this;
    }

    public JsonBuilder rainbow() throws JSONException {
        wrapper.put("rainbow", "0");
        return this;
    }

    public JsonBuilder blink(int duration) throws JSONException {
        wrapper.put("blink", duration);
        return this;
    }

    public JSONObject build() throws JSONException {
        JSONObject led = new JSONObject();
        led.put("led", wrapper);
        return led;
    }

    private String wrapInApostorphe(String string){
        return "\"" + string + "\"";
    }

    private String wrapInApostorphe(int number){
        return "\'" + String.valueOf(number) + "\'";
    }
}

package com.arkas.smarthomecontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hatzo on 27.04.2018.
 */

public class SharedPreferencesHelper {

    private Context c;
    private List<LedListItem> listOfLeds;
    final private String key = "LEDS";

    public SharedPreferencesHelper(Context c) {
        this.c = c;
    }

    public List<LedListItem> getListFromSharedPreferences() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);

        Gson gson = new Gson();
        String response = sharedPref.getString(key, "");
        List<LedListItem> listOfLedsFromSP = gson.fromJson(response,
                new TypeToken<List<LedListItem>>() {
                }.getType());

        if (listOfLedsFromSP == null) {
            listOfLedsFromSP = new ArrayList<>();
        }

        this.listOfLeds = listOfLedsFromSP;

        return listOfLeds;
    }

    public void addItemToSharedPreferences(LedListItem item) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);

        if (this.listOfLeds == null) {
            this.listOfLeds = new ArrayList<LedListItem>();
        }
        listOfLeds.add(item);

        SharedPreferences.Editor editor;

        Gson gson = new Gson();
        String json = gson.toJson(listOfLeds);
        Log.i("SharedPreferencesHelper", "Produced JSON: " + json);

        editor = sharedPref.edit();
        editor.remove(key).apply();  //template was commit instead of apply
        editor.putString(key, json);
        editor.commit();
    }


    public List<LedListItem> deleteItemFromList(int position) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);

        listOfLeds.remove(position);

        SharedPreferences.Editor editor;

        Gson gson = new Gson();
        String json = gson.toJson(listOfLeds);
        Log.i("SharedPreferencesHelper", "Produced JSON: " + json);

        editor = sharedPref.edit();
        editor.remove(key).apply();  //template was commit instead of apply
        editor.putString(key, json);
        editor.commit();
        return listOfLeds;
    }

    //TODO: implement this method
    /*public void deleteAllItems(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);

        SharedPreferences.Editor editor;

        Gson gson = new Gson();
        String json = gson.toJson(listOfLeds);
        Log.i("SharedPreferencesHelper", "Produced JSON: " + json);

        editor = sharedPref.edit();
        editor.remove(key).apply();  //template was commit instead of apply
        editor.putString(key, "");
        editor.commit();
    }*/

}

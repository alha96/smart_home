package com.arkas.smarthomecontroller.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arkas.smarthomecontroller.mqtt.MqttHelper;
import com.arkas.smarthomecontroller.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {

    private Context c;
    private SharedPreferences.OnSharedPreferenceChangeListener spChangedListener;
    private SharedPreferences sharedPref;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(android.R.color.white));
        c = getActivity().getApplicationContext();


        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(c);

        spChangedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.i("SettingsFragment", "SharedPreferences changed. Destroying MqttHelper instance.");
                Toast.makeText(c, R.string.settings_saved, Toast.LENGTH_LONG).show();
                MqttHelper.destroyInstance();
            }
        };
        sharedPref.registerOnSharedPreferenceChangeListener(spChangedListener);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}

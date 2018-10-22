package com.arkas.smarthomecontroller.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.arkas.smarthomecontroller.LedListItem;
import com.arkas.smarthomecontroller.R;
import com.arkas.smarthomecontroller.SharedPreferencesHelper;
import com.arkas.smarthomecontroller.json.JSONBuilder;
import com.arkas.smarthomecontroller.mqtt.MqttHelper;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EffectsFragment extends Fragment {

    GridLayout gl;
    View rootView;

    Button selectedButtonLed;
    String selectedButtonName;
    String selectedButtonTopic;

    private MqttHelper helper;


    public EffectsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (View) inflater.inflate(R.layout.fragment_effects, container, false);

        selectedButtonLed = new Button(getActivity());
        gl = (GridLayout) rootView.findViewById(R.id.grid_layout_led_choose_effects);

        try {
            Log.i("Colorpicker", "Getting the instance");
            helper = MqttHelper.getInstance();
        } catch (MqttException e) {
            e.printStackTrace();
            Log.i("Colorpicker", "Couldn't get instance of MqttHelper.");
            Toast.makeText(getActivity().getApplicationContext(), R.string.not_connected, Toast.LENGTH_LONG).show();
        }


        Log.i("EffectsFragment", "Effects Fragment opened!");

        SharedPreferencesHelper sph = new SharedPreferencesHelper(getActivity());
        List<LedListItem> listOfLeds = sph.getListFromSharedPreferences();

        for (int i = 0; i < listOfLeds.size(); i++) {

            final Button currentBtn = new Button(getActivity());
            final String btnName = listOfLeds.get(i).getName();
            final String topicOfBtn = listOfLeds.get(i).getTopic();
            currentBtn.setText(btnName);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f));

            layoutParams.width = 0;
            currentBtn.setLayoutParams(layoutParams);

            currentBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectedButtonLed = currentBtn;
                    selectedButtonName = btnName;
                    selectedButtonTopic = topicOfBtn;
                    resetGLButtons();
                    currentBtn.setBackground(getResources().getDrawable(R.drawable.bg_button));
                }
            });

            gl.addView(currentBtn);

        }


        final Button rainbowBtn = rootView.findViewById(R.id.button_effect_rainbow);
        rainbowBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectedButtonName == null) {
                    showWarning();
                } else {
                    resetEffectButtons();
                    rainbowBtn.setBackground(getResources().getDrawable(R.drawable.bg_button));
                    String json = new JSONBuilder().range(0, 1).rainbow().build().toString();
                    helper.sendMessage(selectedButtonTopic, json);
                    Toast.makeText(getActivity(), "Effect 'Rainbow' sent to LED " + selectedButtonName + "!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button kittBtn = rootView.findViewById(R.id.button_effect_kitt);
        kittBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectedButtonName == null) {
                    showWarning();
                } else {
                    resetEffectButtons();
                    kittBtn.setBackground(getResources().getDrawable(R.drawable.bg_button));
                    String json = new JSONBuilder().range(0, 1).kitt(30, 235).build().toString();
                    helper.sendMessage(selectedButtonTopic, json);
                    Toast.makeText(getActivity(), "Effect 'KnightRider' sent to LED " + selectedButtonName + "!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button theaterBtn = rootView.findViewById(R.id.button_effect_theater);
        theaterBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectedButtonName == null) {
                    showWarning();
                } else {
                    Toast.makeText(getActivity(), "Not implemented yet!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button chaserBtn = rootView.findViewById(R.id.button_effect_chaser);
        chaserBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectedButtonName == null) {
                    showWarning();
                } else {
                    //Toast.makeText(getActivity(), "Effect 'Chaser' sent to LED " + selectedButtonName + "!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "Not implemented yet!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private void showWarning() {
        Toast.makeText(getActivity(), "Please select a LED first!", Toast.LENGTH_SHORT).show();

    }

    private void resetGLButtons() {
        gl = (GridLayout) rootView.findViewById(R.id.grid_layout_led_choose_effects);
        for (int i = 0; i < gl.getChildCount(); i++) {
            Button currentBtn = (Button) gl.getChildAt(i);
            currentBtn.setBackgroundResource(android.R.drawable.btn_default);
        }
    }

    private void resetEffectButtons() {
        Button rainbowBtn = rootView.findViewById(R.id.button_effect_rainbow);
        rainbowBtn.setBackgroundResource(android.R.drawable.btn_default);

        Button kittBtn = rootView.findViewById(R.id.button_effect_kitt);
        kittBtn.setBackgroundResource(android.R.drawable.btn_default);

        //TODO: add other effects
    }


}

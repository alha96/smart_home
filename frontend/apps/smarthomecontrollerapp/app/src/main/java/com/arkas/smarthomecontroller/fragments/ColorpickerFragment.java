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
import com.arkas.smarthomecontroller.SharedPreferencesHelper;
import com.arkas.smarthomecontroller.mqtt.MqttHelper;
import com.arkas.smarthomecontroller.R;
import com.arkas.smarthomecontroller.json.JSONBuilder;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ColorpickerFragment extends Fragment {

    //TODO: style the buttons (no orange when pressed)

    GridLayout gl;
    View rootView;
    private MqttHelper helper;

    //TODO: some of them might be removed
    Button selectedButtonLed;
    String selectedButtonName;
    String selectedButtonTopic;

    public ColorpickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("Colorpicker", "Colorpicker opened");

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_colorpicker, container, false);

        try {
            Log.i("Colorpicker", "Getting the instance");
            helper = MqttHelper.getInstance();
        } catch (MqttException e) {
            e.printStackTrace();
            Log.i("Colorpicker", "Couldn't get instance of MqttHelper.");
            Toast.makeText(getActivity().getApplicationContext(), R.string.not_connected, Toast.LENGTH_LONG).show();
        }

        selectedButtonLed = new Button(getActivity());
        gl = (GridLayout) rootView.findViewById(R.id.grid_layout_led_choose_colorpicker);


        Log.i("EffectsFragment", "Effects Fragment opened!");

        List<LedListItem> listOfLeds = null;
        SharedPreferencesHelper sph = new SharedPreferencesHelper(getActivity());
        listOfLeds = sph.getListFromSharedPreferences();

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


        ColorPickerView colorpicker = rootView.findViewById(R.id.color_picker_view);
        //TODO: get this lightness slider (and alpha slider) to work
        /*LightnessSlider ls = rootView.findViewById(R.id.v_lightness_slider);
        colorpicker.setLightnessSlider(ls);
        colorpicker.setLightness(0);*/
        colorpicker.addOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int selectedColor) {

                if (selectedColor != -1) {

                    Log.i("Colorpicker", "Selected color: " + selectedColor);

                    int r = (selectedColor >> 16) & 0xFF;
                    int g = (selectedColor >> 8) & 0xFF;
                    int b = selectedColor & 0xFF;

                    JSONBuilder builder = new JSONBuilder();
                    String json = builder.range(0, 1).staticObj(r, g, b).build().toString();

                    Log.i("Colorpicker", "JSON to send: \n" + json);

                    helper.sendMessage(selectedButtonTopic, json);

                } else {
                    throw new IllegalStateException("Selected color is not defined.");
                }

            }
        });

        return rootView;
    }

    private void resetGLButtons(){
        gl = (GridLayout) rootView.findViewById(R.id.grid_layout_led_choose_colorpicker);
        for(int i=0; i<gl.getChildCount(); i++) {
            Button currentBtn = (Button) gl.getChildAt(i);
            currentBtn.setBackgroundResource(android.R.drawable.btn_default);
        }
    }


}

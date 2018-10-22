package com.psudhaus.multitimer.multitimer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, TimerCallback, AdapterView.OnItemLongClickListener {

    private GridView gridView;
    private List<TimerHolder> timersUi;
    private List<NameHolder> nameSelList;

    private TimerAdapter timerAdapter;
    private NameSelectionAdapter nameSelectionAdapter;
    private ListView selectionList;
    private RelativeLayout nameSelection;
    //play/pause button
    private ImageButton btnState;

    //Object to communicate with service
    TimerService mService;
    boolean mBound;
    TimerCallback callback;


    private int selection = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = findViewById(R.id.gridView1);
        nameSelection = findViewById(R.id.nameSelection);
        selectionList = findViewById(R.id.nameSelectionList);

        timersUi = new ArrayList<>();
        //timersUi initialized in onResume
        nameSelList = new LinkedList<>();

        nameSelList.add(new NameHolder("Foo", null));
        nameSelList.add(new NameHolder("Bar", null));
        nameSelList.add(new NameHolder("AHhhh", null));

        timerAdapter = new TimerAdapter(this, timersUi);
        nameSelectionAdapter = new NameSelectionAdapter(this, nameSelList);
        gridView.setAdapter(timerAdapter);
        selectionList.setAdapter(nameSelectionAdapter);

        init();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //turn off view updates
        //unbindService(mService);
        mService.setCallback(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        selection = -1;

        Intent serviceIntent = new Intent(this, TimerService.class);
        Log.d("FOO" , "2");
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d("FOO" , "3");
        startService(serviceIntent);
        Log.d("FOO" , "4");



        //now got timers
        //even if service already started just start it again

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBound = false;
    }


    private void init() {
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);

        findViewById(R.id.btn_add_timer).setOnClickListener(this);
        findViewById(R.id.btn_add_time_0010).setOnClickListener(this);
        findViewById(R.id.btn_add_time_0100).setOnClickListener(this);
        findViewById(R.id.btn_add_time_0500).setOnClickListener(this);
        findViewById(R.id.btn_add_time_2000).setOnClickListener(this);
        findViewById(R.id.btn_add_time_n0100).setOnClickListener(this);
        findViewById(R.id.btn_add_time_n0100).setOnClickListener(this);
        findViewById(R.id.btn_opt_delete).setOnClickListener(this);
        findViewById(R.id.btn_opt_pause).setOnClickListener(this);
        findViewById(R.id.btn_opt_reset).setOnClickListener(this);

        btnState = findViewById(R.id.btn_opt_pause);

        callback = this;

    }

    @Override
    public void onClick(View view) {
        Log.d("Click", "Currently selected: " + selection);
        switch (view.getId()){
            case R.id.btn_add_timer:
                Log.d("Clicked", "Add timer");
                TimerHolder newTimer = new TimerHolder(0, "Timer " + String.valueOf(timersUi.size() + 1), true);
                mService.createTimer(newTimer);
                timersUi.add(newTimer);
                //timersUi = mService.getTimers(;

                logTimersUi();
                selection = timersUi.size() -1;
                updateSelected();
                break;
            case R.id.btn_add_time_0010:
                mService.updateAddSeconds(selection, 10);
                break;
            case R.id.btn_add_time_0100:
                mService.updateAddSeconds(selection, 60);
                break;
            case R.id.btn_add_time_0500:
                mService.updateAddSeconds(selection, 300);
                break;
            case R.id.btn_add_time_2000:
                mService.updateAddSeconds(selection, (20*60));
                break;
            case R.id.btn_add_time_n0100:
                mService.updateAddSeconds(selection, -60);
                break;
            case R.id.btn_opt_delete:
                deleteTimer(selection);
                break;
            case R.id.btn_opt_pause:
                //UI state is used not state of timerService
                if (mService.getTimers().get(selection).getStartTimeMs() == 0){
                    mService.getTimers().get(selection).setStartTimeMs(mService.getTimers().get(selection).getRestTimeMs());
                }
                mService.updateIsRunning(selection, !timersUi.get(selection).isRunning());
                updateGridTimers();
                break;
            case R.id.btn_opt_reset:
                //TODO
                resetTimer(selection);
                break;
        }


        timerAdapter.notifyDataSetChanged();

        //updateGridTimers();
        for (TimerHolder timerHolder : timersUi) {
            Log.d("Timer", timerHolder.toString());
        }

    }

    private void logTimersUi() {
        for (TimerHolder timerHolder : timersUi) {
            Log.d("Timer: " + timerHolder.getName(), timerHolder.toString());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long l) {
        Log.i("Item click", "Item click in " + pos);
        if (parent.getId() == R.id.gridView1) {
            timerItemSelected(pos);
        } else if (parent.getId() == R.id.nameSelectionList){

        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
        Log.d("Long click", String.valueOf(pos));
        //Grid item clicked
        if (parent.getId() == R.id.gridView1){
            selection = pos;
            updateSelected();
            Log.d("click", "Clicked " + selection);
        }

        return false;
    }

    /**
     * Called from Timer gridView item
     *
     * @param view
     */
    public void onBtnTimerRestartClicked(View view) {
        if ( view instanceof ImageView){
            Object tag = view.getTag();
            if (tag instanceof Integer){
                int id = ((Integer) tag).intValue();
                resetTimer(id);
            }
        }
    }

    /**
     * Called from Timer gridView item
     *
     * @param view
     */
    public void onBtnTimerDeleteClicked(View view) {
        if ( view instanceof ImageView){
            Object tag = view.getTag();
            if (tag instanceof Integer){
                int id = ((Integer) tag).intValue();
                deleteTimer(id);
            }
        }
    }




    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {

            Log.d("Connection", "Service Connected");
            Toast.makeText(getApplicationContext(), "Service Connected", Toast.LENGTH_SHORT).show();
            TimerService.ServiceBinder binder = (TimerService.ServiceBinder) service;
            mService = binder.getService();
            mService.setCallback(callback);

           // timersUi = new ArrayList<>();
//            for (TimerHolder timer : mService.getTimers()) {
//                TimerHolder newTimer = new TimerHolder(timer.getRestTimeSec(), timer.getName(), false);
//
//                timersUi.add(newTimer);
//            }

            for (TimerHolder timer : timersUi) {
                Log.d("Timer", timer.toString());
            }


            Log.d("FOO", "mService initialized");
            updateGridTimers();
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, it's process crashed.
           // mService = null;
            mBound = false;
            mService.setCallback(null);
            Log.d("Connection", "Service Disconnected");
        }

    };

    private void updateGridTimers() {
        timerAdapter.notifyDataSetChanged();
        gridView.invalidateViews();

        if (selection == -1) {
            btnState.setVisibility(View.INVISIBLE);
        } else {
            btnState.setVisibility(View.VISIBLE);
            int iconDrawable;
            if (timersUi.get(selection).isRunning()) {
                iconDrawable = R.drawable.ic_pause;
            } else {
                iconDrawable = R.drawable.ic_play;
            }
            btnState.setImageDrawable(ContextCompat.getDrawable(this,iconDrawable));
        }

    }

    private void updateSelected() {
        for (int i = 0; i < timersUi.size(); i++) {
            timersUi.get(i).setSelected(i == selection);
        }
        updateGridTimers();
    }

    @Override
    public void timerUpdated() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //timersUi = mService.getTimers();
                updateGridTimers();
            }
        });
    }

    @Override
    public void timerDoneAlarm(int id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplication().getBaseContext(), "Timer " + " done!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void deleteTimer(int id){
        Log.i("Click", "Timer Delete ID " + id);
        if (id != -1 && id >= 0 && id < timersUi.size() ){
            mService.removeTimer(id);
            timersUi.remove(id);
            if (selection == id)
                selection = -1;
            else if (id < selection){
                //if an item before the selection dissapears change the focus
                selection--;
            }
            //otherwise leave selection
            updateSelected();
        }
    }
    public void resetTimer(int id){
        Log.i("Click", "Reset Timer ID " + id);
        if (id != -1 && id >= 0 && id < timersUi.size() ){
            TimerHolder timer = mService.getTimers().get(id);
            timer.setRestTimeMs(timer.getStartTimeMs());
            timersUi.get(id).setRunning(false);
            timer.setDone(false);
        }
        //set to current item
        timerItemSelected(id);
    }

    public void timerItemSelected(int id){
        selection = id;
        updateSelected();
        if (timersUi.get(id).isDone()){
            //if currently ringing --> turn off
            timersUi.get(id).setDone(false);
            //handle (delete or reset)
            Toast.makeText(getApplicationContext(), "Timer " + id + " ringing stopped",Toast.LENGTH_SHORT).show();
        }
        Log.d("Timer Item", "New selection: " + selection);
    }
}

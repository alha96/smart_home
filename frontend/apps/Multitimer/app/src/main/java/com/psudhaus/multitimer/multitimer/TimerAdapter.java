package com.psudhaus.multitimer.multitimer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by psudh on 08-Mar-18.
 */

public class TimerAdapter extends BaseAdapter {
    private Context context;
    private List<TimerHolder> timerData;

    public TimerAdapter(Context context, List<TimerHolder> timerInput) {
        this.context = context;
        this.timerData = timerInput;

        for (TimerHolder timerHolder : timerInput) {
        Log.d("IN ADAPTER", timerHolder.toString());

        }


    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TimerViewHolder holder;

        if (convertView == null) {
            // get layout from mobile.xml
            convertView = inflater.inflate(R.layout.timer_display, parent, false);

            holder = new TimerViewHolder();
            holder.time = convertView.findViewById(R.id.tv_timer_clock);
            holder.desc = convertView.findViewById(R.id.tv_timer_desc);
            holder.layout = convertView.findViewById(R.id.timer_frame);
            holder.pulseAlarm = convertView.findViewById(R.id.pulsatorTimer);
            holder.restartTimer = convertView.findViewById(R.id.ibTimerRestart);
            holder.deleteTimer = convertView.findViewById(R.id.ibTimerDelete);
            convertView.setTag(holder);
        } else {
            holder = (TimerViewHolder) convertView.getTag();
        }



        TimerHolder item = timerData.get(position);

        Log.d("IN ADAPTER", item.toString());

        holder.restartTimer.setTag(position);
        holder.deleteTimer.setTag(position);
        holder.desc.setTag(position);
        holder.time.setText(item.getTimeStr());
        holder.desc.setText(item.getName());
        if (item.isDone()){
            holder.pulseAlarm.start();
        } else {
            holder.pulseAlarm.stop();
        }

        int color;
        Resources res = context.getResources();
        if (item.getRestTimeMs() == 0 && item.getStartTimeMs() != 0) {
            //timer has already run and is sitting there after being turned off
            holder.deleteTimer.setVisibility(View.VISIBLE);
            holder.restartTimer.setVisibility(View.VISIBLE);
        } else {
            holder.deleteTimer.setVisibility(View.GONE);
            holder.restartTimer.setVisibility(View.GONE);
        }
        if (!item.isCountDown()) {
            //if counting up
            color = res.getColor(R.color.timerDefault);
        } else if (item.isDone()){
            //is blinking and alarm ringing.
            color = res.getColor(R.color.timerWarn);
            holder.deleteTimer.setVisibility(View.VISIBLE);
            holder.restartTimer.setVisibility(View.VISIBLE);
        } else if (!item.isRunning()) {
            //if paused
            color = res.getColor(R.color.timerDefault);
        }  else if (item.getRestTimeSec() >= 60){

            //default
            color = res.getColor(R.color.timerDefault);
        } else if (item.getRestTimeSec() < 60 && item.getRestTimeSec() > 0) {
            //last minute
            color = res.getColor(R.color.timerWarn);
        } else {
            //should never occur
            color = res.getColor(R.color.colorPrimary);
        }

        if (timerData.get(position).isSelected()){
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(color); // Changes this drawbale to use a single color instead of a gradient
            gd.setCornerRadius(5);
            gd.setStroke(15, 0);
            holder.layout.setBackground(gd);
        } else {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(color);
            holder.layout.setBackground(gd);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return timerData.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < 0 || position > timerData.size() - 1)
            return null;
        else
            return timerData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}

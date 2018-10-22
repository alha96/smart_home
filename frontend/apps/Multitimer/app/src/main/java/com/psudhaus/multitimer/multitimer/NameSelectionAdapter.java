package com.psudhaus.multitimer.multitimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by psudh on 16-Apr-18.
 */

public class NameSelectionAdapter extends BaseAdapter {

    private Context c;
    private List<NameHolder> nameData;

    public NameSelectionAdapter(Context context, List<NameHolder> nameData){
        this.c = context;
        this.nameData = nameData;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        NameViewHolder holder;

        if (convertView == null){
            convertView = inflater.inflate(R.layout.name_item, null);

            holder = new NameViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tvNameName);
            holder.iv = (ImageView) convertView.findViewById(R.id.ivNameIcon);
            convertView.setTag(holder);
        } else {
            holder = (NameViewHolder) convertView.getTag();
        }

        holder.name.setText(nameData.get(pos).getName());
        //TODO set img src

        return convertView;
    }

    @Override
    public int getCount() {
        return nameData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}

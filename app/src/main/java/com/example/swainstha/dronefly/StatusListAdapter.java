package com.example.swainstha.dronefly;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by swainstha on 5/27/18.
 */

public class StatusListAdapter extends ArrayAdapter<StatusData> {

    Context context;
    private ArrayList<StatusData> statusList;

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView value;
    }

    public StatusListAdapter(@NonNull Context context, ArrayList<StatusData> list) {
        super(context, 0, list);
        this.context = context;
        this.statusList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // to add a custom view..

        StatusListAdapter.ViewHolder viewHolder;
        StatusData statusData= getItem(position);
        final View result;

        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.status_list, parent, false);

            viewHolder = new StatusListAdapter.ViewHolder();

            viewHolder.title = convertView.findViewById(R.id.item_title);
            viewHolder.value = convertView.findViewById(R.id.item_value);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (StatusListAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        // Set the border of View (ListView Item)
        convertView.setBackground(getContext().getDrawable(R.drawable.list_border));
        viewHolder.title.setText(statusData.getTitle());
        viewHolder.value.setText(statusData.getValue());

        // making view clickable
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               StatusListAdapter.ViewHolder v = (StatusListAdapter.ViewHolder)view.getTag();

            }
        });
        return convertView;
    }
}

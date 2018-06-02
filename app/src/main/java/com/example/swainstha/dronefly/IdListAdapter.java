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
 * Created by swainstha on 6/1/18.
 */

public class IdListAdapter extends ArrayAdapter<IdList> {

    Context context;
    private ArrayList<IdList> idList;

// View lookup cache
    private static class ViewHolder {
        TextView id;
        TextView value;
    }

    public IdListAdapter(@NonNull Context context, ArrayList<IdList> list) {
        super(context, 0, list);
        this.context = context;
        this.idList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // to add a custom view..

        IdListAdapter.ViewHolder viewHolder;
        IdList idList1 = getItem(position);
        final View result;

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.id_list, parent, false);

            viewHolder = new IdListAdapter.ViewHolder();

            viewHolder.id = convertView.findViewById(R.id.id_copter);
            viewHolder.value = convertView.findViewById(R.id.id_value);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (IdListAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        // Set the border of View (ListView Item)
        convertView.setBackground(getContext().getDrawable(R.drawable.list_border));
        viewHolder.id.setText(idList1.getId());
        viewHolder.value.setText(idList1.getValue());

        // making view clickable
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IdListAdapter.ViewHolder v = (IdListAdapter.ViewHolder) view.getTag();

            }
        });
        return convertView;
    }
}


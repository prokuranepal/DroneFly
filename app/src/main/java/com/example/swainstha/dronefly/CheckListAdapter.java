package com.example.swainstha.dronefly;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by swainstha on 5/30/18.
 */

public class CheckListAdapter extends BaseAdapter {

    Context context;
    private ArrayList<CheckList> checkList;
    private ArrayList<Boolean> boolCheck;
    private CheckBox checkBox;
    private boolean check = true;

    public interface PictureClickListener {
        public void onPictureClick(boolean c);
    }

    private PictureClickListener listener;

    public void setPictureClickListener(PictureClickListener listener) {
        this.listener = listener;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        CheckBox check;
    }

    public CheckListAdapter(@NonNull Context context, ArrayList<CheckList> list) {
        this.context = context;
        this.checkList = list;
    }

    @Override
    public int getCount() {
        return checkList.size();
    }

    @Override
    public Object getItem(int position) {
        return checkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    CheckList getCheckList(int position) {
        return ((CheckList) getItem(position));
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // to add a custom view..

        final CheckListAdapter.ViewHolder viewHolder;
        final CheckList checkList1= getCheckList(position);
        final View result;

        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
            convertView = layoutInflater.inflate(R.layout.check_list, parent, false);

            viewHolder = new CheckListAdapter.ViewHolder();

            viewHolder.name = convertView.findViewById(R.id.check_names);
            viewHolder.check = convertView.findViewById(R.id.check_box);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (CheckListAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        // Set the border of View (ListView Item)
        convertView.setBackground(context.getDrawable(R.drawable.list_border));
        viewHolder.name.setText(checkList1.getName());
        viewHolder.check.setChecked(checkList1.isCheck());
        viewHolder.check.setTag(position);

        viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getCheckList((Integer)viewHolder.check.getTag()).setCheck(isChecked);
                //checkList.add(position,getCheckList(position));
                check = true;
                for(CheckList c: checkList) {
                    if(!c.isCheck()) {
                        check = false;
                    }

                }

                Log.i("INFO",check + "");

                if(check == true)
                    listener.onPictureClick(true);
                else
                    listener.onPictureClick(false);


            }
        });
        // making view clickable
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckListAdapter.ViewHolder v = (CheckListAdapter.ViewHolder)view.getTag();


            }
        });
        return convertView;
    }

}

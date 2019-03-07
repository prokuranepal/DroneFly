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
    private ArrayList<CheckList> list;
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
        TextView text;
        CheckBox checkbox;
    }

    public CheckListAdapter(@NonNull Context context, ArrayList<CheckList> list) {
        this.context = context;
        this.list = list;
//        Log.i("checklistAdapter", Boolean.toString(list.get(0).isCheck()));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    CheckList getCheckList(int position) {
        return ((CheckList) getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.check_list, null);
            convertView.setBackground(context.getDrawable(R.drawable.list_border));
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.check_names);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.check_box);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    list.get(getPosition).setCheck(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                    check = true;
                    for(CheckList c: list) {
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
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.check_names, viewHolder.text);
            convertView.setTag(R.id.check_box, viewHolder.checkbox);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.checkbox.setTag(position); // This line is important.

        viewHolder.text.setText(list.get(position).getName());
        viewHolder.checkbox.setChecked(list.get(position).isCheck());

        return convertView;
    }

//    @NonNull
//    @Override
//    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//
//        // to add a custom view..
//
//        final CheckListAdapter.ViewHolder viewHolder;
//        final CheckList checkList1= getCheckList(position);
//        final View result;
//
//        if(convertView == null) {
//            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
//            convertView = layoutInflater.inflate(R.layout.check_list, parent, false);
//
//            viewHolder = new CheckListAdapter.ViewHolder();
//
//            viewHolder.text = convertView.findViewById(R.id.check_names);
//            viewHolder.checkbox = convertView.findViewById(R.id.check_box);
//
//            convertView.setTag(viewHolder);
//
//        } else {
//            viewHolder = (CheckListAdapter.ViewHolder) convertView.getTag();
//            result=convertView;
//        }
//
//        // Set the border of View (ListView Item)
//        convertView.setBackground(context.getDrawable(R.drawable.list_border));
//        viewHolder.text.setText(checkList1.getName());
//        viewHolder.checkbox.setChecked(checkList1.isCheck());
//        viewHolder.checkbox.setTag(position);
//
//        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                getCheckList((Integer)viewHolder.checkbox.getTag()).setCheck(isChecked);
////                checkList.add(position,getCheckList(position));
//                check = true;
//                for(CheckList c: list) {
//                    if(!c.isCheck()) {
//                        check = false;
//                    }
//                }
//                Log.i("INFO",check + "");
//
//                if(check == true)
//                    listener.onPictureClick(true);
//                else
//                    listener.onPictureClick(false);
//
//
//            }
//        });
//        // making view clickable
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                CheckListAdapter.ViewHolder v = (CheckListAdapter.ViewHolder)view.getTag();
//
//
//            }
//        });
//        return convertView;
//    }

}


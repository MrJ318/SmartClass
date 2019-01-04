package com.jnxxgc.smartclass.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jnxxgc.smartclass.R;

import java.util.ArrayList;

public class CheckAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> list_DisplayName;
    private ArrayList<String> list_Status;

    public CheckAdapter(Context context, ArrayList<String> disname, ArrayList<String> statu) {
        mContext = context;
        list_DisplayName = disname;
        list_Status = statu;
    }

    @Override
    public int getCount() {
        return list_DisplayName.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView txt_Name, txt_Status;
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_listview_check, null);
            txt_Name = convertView.findViewById(R.id.textViewcname);
            txt_Status = convertView.findViewById(R.id.textViewcinfo);
            holder = new ViewHolder();
            holder.txt_Name = txt_Name;
            holder.txt_Status = txt_Status;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            txt_Name = holder.txt_Name;
            txt_Status = holder.txt_Status;
        }
        String statu = list_Status.get(position);
        txt_Name.setText(list_DisplayName.get(position));
        txt_Status.setText(statu);
        return convertView;
    }

    class ViewHolder {
        TextView txt_Name, txt_Status;
    }

}

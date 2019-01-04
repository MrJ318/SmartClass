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

public class QueryAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> list_Date;
	private ArrayList<String> list_Item;
	private ArrayList<String> list_Info;

	public QueryAdapter(Context context, ArrayList<String> date, ArrayList<String> item, ArrayList<String> info) {
		mContext = context;
		list_Date = date;
		list_Item = item;
		list_Info = info;
	}

	@Override
	public int getCount() {
		return list_Date.size();
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

		TextView txt_Date, txt_Item, txt_Info;
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_listview_query, null);
			txt_Date =  convertView.findViewById(R.id.textViewqdate);
			txt_Item =  convertView.findViewById(R.id.textViewqitem);
			txt_Info =  convertView.findViewById(R.id.textViewqinfo);
			holder = new ViewHolder();
			holder.txt_Date = txt_Date;
			holder.txt_Item = txt_Item;
			holder.txt_Info = txt_Info;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			txt_Date = holder.txt_Date;
			txt_Item = holder.txt_Item;
			txt_Info = holder.txt_Info;
		}

		txt_Date.setText(list_Date.get(position));
		txt_Item.setText(list_Item.get(position));
		txt_Info.setText(list_Info.get(position));
		return convertView;
	}

	class ViewHolder {
		TextView txt_Date, txt_Item, txt_Info;
	}

}

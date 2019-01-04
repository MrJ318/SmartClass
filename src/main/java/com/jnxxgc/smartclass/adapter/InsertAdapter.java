package com.jnxxgc.smartclass.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jnxxgc.smartclass.R;

import java.util.ArrayList;

public class InsertAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> list_DisplayName;
	private ArrayList<String> list_Status;

	public InsertAdapter(Context context, ArrayList<String> disname, ArrayList<String> statu) {
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

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Viewholder holder;
		TextView txt_Name;
		RadioButton rb_yd, rb_cd, rb_wd, rb_qj;

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_listview_insert, null);
			txt_Name =  convertView.findViewById(R.id.textViewiname);
			rb_yd =  convertView.findViewById(R.id.radioButton1);
			rb_cd =  convertView.findViewById(R.id.radioButton2);
			rb_wd =  convertView.findViewById(R.id.radioButton3);
			rb_qj =  convertView.findViewById(R.id.radioButton4);

			holder = new Viewholder();
			holder.txt_Name = txt_Name;
			holder.rb1 = rb_yd;
			holder.rb2 = rb_cd;
			holder.rb3 = rb_wd;
			holder.rb4 = rb_qj;
			convertView.setTag(holder);
		} else {
			holder = (Viewholder) convertView.getTag();
			txt_Name = holder.txt_Name;
			rb_yd = holder.rb1;
			rb_cd = holder.rb2;
			rb_wd = holder.rb3;
			rb_qj = holder.rb4;
		}

		txt_Name.setText(list_DisplayName.get(position));
		String statu = list_Status.get(position);
		switch (statu) {
			case "出勤":
				rb_yd.setChecked(true);
				break;
			case "迟到":
				rb_cd.setChecked(true);
				break;
			case "缺勤":
				rb_wd.setChecked(true);
				break;
			case "请假":
				rb_qj.setChecked(true);
				break;
		}

		rb_yd.setOnClickListener(v -> list_Status.set(position, "出勤"));
		rb_cd.setOnClickListener(v -> list_Status.set(position, "迟到"));
		rb_wd.setOnClickListener(v -> list_Status.set(position, "缺勤"));
		rb_qj.setOnClickListener(v -> list_Status.set(position, "请假"));

		return convertView;
	}

	class Viewholder {
		TextView txt_Name;
		RadioButton rb1, rb2, rb3, rb4;
	}
}

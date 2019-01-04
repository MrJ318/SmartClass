package com.jnxxgc.smartclass.thread;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.jnxxgc.smartclass.table.AttendanceInfo;
import com.jnxxgc.smartclass.util.Constant;
import com.jnxxgc.smartclass.util.ToHtml;

import java.util.ArrayList;
import java.util.List;

public class SummaryThread extends Thread {

	private List<String> list_Item;
	private List<String> list_Date;
	private List<AttendanceInfo> list_WeekData;
	private Handler mHandler;
	private ToHtml html;
	private Context mContext;

	public SummaryThread(Context context, List<String> dates, List<AttendanceInfo> datas, Handler handler) {
		mContext = context;
		list_WeekData = datas;
		list_Item = getAllItems();
		list_Date = dates;
		mHandler = handler;
	}

	@Override
	public void run() {
		dataPacking();
		mHandler.sendEmptyMessage(Constant.WHAT_SAVE);
	}

	/*
	 * 获取数据中的所有条目
	 */
	private ArrayList<String> getAllItems() {
		ArrayList<String> items = new ArrayList<String>();
		for (AttendanceInfo att : list_WeekData) {
			boolean flag = false;
			for (String s : items) {
				if (s.equals(att.getItem())) {
					flag = true;
				}
			}
			if (!flag) {
				items.add(att.getItem());
			}
		}
		return items;
	}

	private void dataPacking() {

		list_Item = getAllItems();
		html = new ToHtml(list_WeekData.get(0).getClassOf(), list_Date.get(0), list_Date.get(4));

		/*
		 * 根据项目名称分成若干组
		 */
		ArrayList<List<AttendanceInfo>> list_ByItem = new ArrayList<List<AttendanceInfo>>();
		for (int i = 0; i < list_Item.size(); i++) {
			List<AttendanceInfo> list = new ArrayList<AttendanceInfo>();
			for (AttendanceInfo a : list_WeekData) {
				if (list_Item.get(i).equals(a.getItem())) {
					list.add(a);
				}
			}
			list_ByItem.add(list);
		}

		/*
		 * 数据打包
		 *
		 */
		ArrayList<ArrayList<List<AttendanceInfo>>> list_b = new ArrayList<>();
		for (List<AttendanceInfo> la : list_ByItem) {
			ArrayList<List<AttendanceInfo>> list_a = new ArrayList<>();

			// 将每天的数据分别装入以下数组
			List<AttendanceInfo> list_1 = new ArrayList<>();
			List<AttendanceInfo> list_2 = new ArrayList<>();
			List<AttendanceInfo> list_3 = new ArrayList<>();
			List<AttendanceInfo> list_4 = new ArrayList<>();
			List<AttendanceInfo> list_5 = new ArrayList<>();
			for (int i = 0; i < la.size(); i++) {
				if (la.get(i).getDate().equals(list_Date.get(0))) {
					list_1.add(la.get(i));
				} else if (la.get(i).getDate().equals(list_Date.get(1))) {
					list_2.add(la.get(i));
				} else if (la.get(i).getDate().equals(list_Date.get(2))) {
					list_3.add(la.get(i));
				} else if (la.get(i).getDate().equals(list_Date.get(3))) {
					list_4.add(la.get(i));
				} else if (la.get(i).getDate().equals(list_Date.get(4))) {
					list_5.add(la.get(i));
				}
			}
			// 将五天的数据打包到一个数组中
			list_a.add(list_1);
			list_a.add(list_2);
			list_a.add(list_3);
			list_a.add(list_4);
			list_a.add(list_5);
			// 将多个项目打包到一起
			list_b.add(list_a);
			Log.d("Jevon", "*" + list_1.size() + "*" + list_2.size() + "*" + list_3.size() + "*" + list_4.size() + "*"
					+ list_5.size());
			Log.d("Jevon", "*a" + list_a.size());
			Log.d("Jevon", "*b" + list_b.size());

		}

		/*
		 * 数据拆包
		 */
		for (int i = 0; i < list_b.size(); i++) {
			// for (ArrayList<List<AttendanceInfo>> ala : list_b) {
			ArrayList<List<AttendanceInfo>> ala = list_b.get(i);
			ArrayList<String> ast = new ArrayList<String>();
			for (List<AttendanceInfo> la : ala) {

				Log.d("Jevon", "return++");
				if (la.size() == 0) {
					Log.d("Jevon", "return");
					ast.add(" ");
					continue;
				}
				StringBuilder wei = new StringBuilder("缺勤：<br />");
				StringBuilder chi = new StringBuilder("迟到：<br />");
				StringBuilder jia = new StringBuilder("请假：<br />");
				for (AttendanceInfo a : la) {
					if (a.getSituation().equals("缺勤")) {
						wei.append(a.getName() + ",");
					} else if (a.getSituation().equals("迟到")) {
						chi.append(a.getName() + ",");
					} else if (a.getSituation().equals("请假")) {
						jia.append(a.getName() + ",");
					}
				}

				if (wei.length() < 4) {
					wei = new StringBuilder(" ");
				}
				if (chi.length() < 4) {
					chi = new StringBuilder(" ");
				}
				if (jia.length() < 4) {
					jia = new StringBuilder(" ");
				}
				String str = wei + "<br>" + chi + "<br>" + jia;
				ast.add(str);
				Log.d("Jevon", str);
			}
			html.convert(list_Item.get(i), ast);
		}
		html.commit(mContext.getCacheDir() + "/table.html");
	}

}

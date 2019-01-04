package com.jnxxgc.smartclass.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MyDate {

	public static ArrayList<String> getDates(Calendar today) {

		ArrayList<String> listData = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		int dayofwek = today.get(Calendar.DAY_OF_WEEK);

		switch (dayofwek) {
			// 星期一
			case 2:
				for (int i = 0; i < 5; i++) {
					Calendar c = Calendar.getInstance();
					c.add(Calendar.DAY_OF_MONTH, i);
					listData.add(sdf.format(c.getTime()));
				}
				break;

			case 3:
				for (int i = -1; i < 4; i++) {
					Calendar c = Calendar.getInstance();
					c.add(Calendar.DAY_OF_MONTH, i);
					listData.add(sdf.format(c.getTime()));
				}
				break;

			case 4:
				for (int i = -2; i < 3; i++) {
					Calendar c = Calendar.getInstance();
					c.add(Calendar.DAY_OF_MONTH, i);
					listData.add(sdf.format(c.getTime()));
				}
				break;

			case 5:
				for (int i = -3; i < 2; i++) {
					Calendar c = Calendar.getInstance();
					c.add(Calendar.DAY_OF_MONTH, i);
					listData.add(sdf.format(c.getTime()));
				}
				break;

			case 6:
				for (int i = -4; i < 1; i++) {
					Calendar c = Calendar.getInstance();
					c.add(Calendar.DAY_OF_MONTH, i);
					listData.add(sdf.format(c.getTime()));
				}
				break;

			case 7:
				for (int i = -5; i < 0; i++) {
					Calendar c = Calendar.getInstance();
					c.add(Calendar.DAY_OF_MONTH, i);
					listData.add(sdf.format(c.getTime()));
				}
				break;

			// 星期天
			case 1:
				for (int i = -6; i < -1; i++) {
					Calendar c = Calendar.getInstance();
					c.add(Calendar.DAY_OF_MONTH, i);
					listData.add(sdf.format(c.getTime()));
				}
				break;
		}

		return listData;
	}

}

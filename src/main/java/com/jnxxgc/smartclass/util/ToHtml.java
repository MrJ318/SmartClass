package com.jnxxgc.smartclass.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ToHtml {

	private String mTitle;
	private String mTime;
	private String mHtmlHead;
	private final String mHtmlItem = "<tr> <td>item</td>    <td>one</td>    <td>two</td>  <td>three</td>    <td>four</td>    <td>five</td>  </tr>";
	private final String mHtmlEnd = "</table></body></html>";

	private String result;

	public ToHtml(String title, String start, String end) {
		mTitle = title + "第____周考勤表";
		mTime = "时间:" + start + "-" + end;
		mHtmlHead = "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>班级考勤表</title></head><body></table><p> </p>"
				+ "<table width=\"600\"  border=\"1\">  <caption>  <strong>" + mTitle + "</strong>  <br />" + mTime
				+ "<br /> </caption> "
				+ "<tr> <td width=\"30\"></td>    <td align=\"center\" width=\"114\">周一</td>    <td align=\"center\" width=\"114\">周二</td>  <td align=\"center\" width=\"114\">周三</td>    <td align=\"center\" width=\"114\">周四</td>    <td align=\"center\" width=\"114\">周五</td>  </tr>";
		result = mHtmlHead;
	}

	public void convert(String item, List<String> info) {

		String mid = new String(mHtmlItem);
		mid = mid.replace("item", item);
		mid = mid.replace("one", "" + info.get(0));
		mid = mid.replace("two", "" + info.get(1));
		mid = mid.replace("three", "" + info.get(2));
		mid = mid.replace("four", "" + info.get(3));
		mid = mid.replace("five", "" + info.get(4));
		result += mid;
	}

	public void commit(String path) {
		result += mHtmlEnd;
		saveStringToFile(path, result);
	}

	public boolean saveStringToFile(String path, String content) {
		try {
			FileOutputStream fos = new FileOutputStream(path);
			// 把长宽写入头部
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}

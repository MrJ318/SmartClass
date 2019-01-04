package com.jnxxgc.smartclass.table;

import cn.bmob.v3.BmobObject;

public class AttendanceInfo extends BmobObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String Name;
	private String ClassOf;
	private String Date;
	private String Item;
	private String Situation;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		this.Name = name;
	}

	public String getClassOf() {
		return ClassOf;
	}

	public void setClassOf(String classOf) {
		ClassOf = classOf;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		this.Date = date;
	}

	public String getItem() {
		return Item;
	}

	public void setItem(String item) {
		this.Item = item;
	}

	public String getSituation() {
		return Situation;
	}

	public void setSituation(String situation) {
		this.Situation = situation;
	}


}

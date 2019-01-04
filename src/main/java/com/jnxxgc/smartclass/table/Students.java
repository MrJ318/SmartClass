package com.jnxxgc.smartclass.table;

import cn.bmob.v3.BmobUser;

public class Students extends BmobUser {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String Name; // 姓名
	private String Sex; // 性别
	private String Birth_Date; // 出生日期
	private String Reg_Number; // 学籍号
	private String ClassOf; // 所属班级
	private String Jurisdiction; // 权限

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		this.Name = name;
	}

	public String getSex() {
		return Sex;
	}

	public void setSex(String sex) {
		this.Sex = sex;
	}

	public String getBirth_Date() {
		return Birth_Date;
	}

	public void setBirth_Date(String birth_Date) {
		this.Birth_Date = birth_Date;
	}

	public String getReg_Number() {
		return Reg_Number;
	}

	public void setReg_Number(String reg_Number) {
		this.Reg_Number = reg_Number;
	}

	public String getClassof() {
		return ClassOf;
	}

	public void setClassof(String classof) {
		this.ClassOf = classof;
	}

	public String getJurisdiction() {
		return Jurisdiction;
	}

	public void setJurisdiction(String jurisdiction) {
		this.Jurisdiction = jurisdiction;
	}

}

package com.jnxxgc.smartclass.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ListUtli {

    public ArrayList<String> getList_Date() {

        ArrayList<String> list_Date = new ArrayList<>();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        list_Date.add(sdf.format(c.getTime()));
        c.add(Calendar.DAY_OF_MONTH, -1);
        list_Date.add(sdf.format(c.getTime()));
        c.add(Calendar.DAY_OF_MONTH, -1);
        list_Date.add(sdf.format(c.getTime()));
        return list_Date;
    }


    public ArrayList<String> getList_Item() {
        ArrayList<String> list_Item = new ArrayList<>();
        list_Item.add("早操");
        list_Item.add("早自习");
        list_Item.add("课间操");
        list_Item.add("晚自习");
        return list_Item;
    }


}

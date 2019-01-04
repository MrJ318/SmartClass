package com.jnxxgc.smartclass.util;

import android.util.Log;

public class Jlog {

    public static final String TAG = "Mr.J";
    private static boolean isLog = true;

    public static void d(String tag, String msg) {
        if (isLog) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isLog) {
            Log.e(tag, msg);
        }
    }
}

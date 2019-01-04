package com.jnxxgc.smartclass;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.jnxxgc.smartclass.table.Students;

public class SmartApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private  static Students students;

    public static Students getStudents() {
        return students;
    }

    public static void setStudents(Students students) {
        SmartApplication.students = students;
    }


    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
    }

    public static Context getContext() {
        return context;
    }

}

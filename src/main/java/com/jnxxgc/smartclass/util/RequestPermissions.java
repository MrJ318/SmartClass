package com.jnxxgc.smartclass.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class RequestPermissions {

	private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

	public static boolean getWRITE_EXTERNAL_STORAGE(Context context) {
		if (ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions((Activity) context, new String[] { WRITE_EXTERNAL_STORAGE },
					Constant.REQUEST_PERMISSION_WRITE);
			return false;
		} else {
			return true;
		}
	}

}

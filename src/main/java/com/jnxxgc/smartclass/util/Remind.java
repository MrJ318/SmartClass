package com.jnxxgc.smartclass.util;

import android.content.Context;
import android.widget.Toast;

public class Remind {


	public static void showToast(Context context, String mag) {
		Toast.makeText(context, mag, Toast.LENGTH_SHORT).show();
	}
}

package com.jnxxgc.smartclass;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jnxxgc.smartclass.table.AttendanceInfo;
import com.jnxxgc.smartclass.util.Constant;
import com.jnxxgc.smartclass.util.MyDate;
import com.jnxxgc.smartclass.util.Remind;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ActivityTest extends AppCompatActivity implements View.OnClickListener {


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 3) {
                if (msg.arg1 == 1) {
                    Remind.showToast(ActivityTest.this, "成功");
                    List<AttendanceInfo> list = (List<AttendanceInfo>) msg.obj;
                    ArrayList<String> items = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        String str = list.get(i).getItem();
                        if (!items.contains(str)) {
                            items.add(str);
                        }
                    }
                    File f = new File(Environment.getExternalStorageDirectory(), "test.xls");
                    try {
                        WritableWorkbook workbook = Workbook.createWorkbook(f);
                        WritableSheet sheet = workbook.createSheet("sheet1", 0);
                        int one = 1, two = 2, three = 3, four = 4, five = 5;
                        int l = 1, e = 5;
                        for (int i = 0; i < 4; i++) {
                            Label label1 = new Label(one, 1, "周一");
                            Label label2 = new Label(two, 1, "周二");
                            Label label3 = new Label(three, 1, "周三");
                            Label label4 = new Label(four, 1, "周四");
                            Label label5 = new Label(five, 1, "周五");
                            sheet.addCell(label1);
                            sheet.addCell(label2);
                            sheet.addCell(label3);
                            sheet.addCell(label4);
                            sheet.addCell(label5);
                            sheet.mergeCells(l, 0, e, 0);
                            l += 5;
                            e += 5;
                            one+=5;
                            two+=5;
                            three+=5;
                            four+=5;
                            five+=5;
                        }

                        workbook.write();
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }


                } else {
                    Remind.showToast(ActivityTest.this, "失败");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Bmob.initialize(this, Constant.APPLICATION_ID_D);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            readInternet();
        }
    }

    private void readInternet() {
        new Thread() {
            public void run() {
                BmobQuery<AttendanceInfo> bq = new BmobQuery<>();
                bq.addWhereContainedIn("Date", MyDate.getDates(Calendar.getInstance()));
                bq.addWhereEqualTo("ClassOf", "");
                bq.setLimit(500);
                bq.findObjects(new FindListener<AttendanceInfo>() {

                    @Override
                    public void done(List<AttendanceInfo> list, BmobException e) {
                        Message msg = Message.obtain();
                        msg.what = 3;
                        if (e == null) {
                            msg.arg1 = 1;
                            msg.obj = list;
                        } else {
                            msg.arg1 = 0;
                            msg.obj = e;
                        }
                        mHandler.sendMessage(msg);
                    }
                });

            }
        }.start();
    }
}

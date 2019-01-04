package com.jnxxgc.smartclass;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.Toast;

import com.jnxxgc.smartclass.adapter.InsertAdapter;
import com.jnxxgc.smartclass.table.AttendanceInfo;
import com.jnxxgc.smartclass.table.Students;
import com.jnxxgc.smartclass.util.Constant;
import com.jnxxgc.smartclass.util.ListUtli;
import com.jnxxgc.smartclass.util.Remind;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

public class InsertActivity extends AppCompatActivity implements OnClickListener {

    private EditText edit_Date, edit_Item;
    private ListView listView_Insert;

    private final String mClassOf = SmartApplication.getStudents().getClassof();
    private ArrayList<String> list_Date;
    private ArrayList<String> list_Item;
    private ArrayList<String> list_Name;
    private ArrayList<String> list_Status;
    private ListPopupWindow listPopupWindow;
    private ProgressDialog dialog;
    private MyHandler mMyHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        initView();
        new GetStudentsThread().start();
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.toolbar_insert);
        setSupportActionBar(toolbar);

        dialog = new ProgressDialog(InsertActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("正在加载，请稍后...");
        dialog.show();

        mMyHandler = new MyHandler();
        listPopupWindow = new ListPopupWindow(this);
        listView_Insert = findViewById(R.id.listViewinsert);
        edit_Date = findViewById(R.id.editdate_insert);
        edit_Date.setOnClickListener(this);
        edit_Item = findViewById(R.id.edititem_insert);
        edit_Item.setOnClickListener(this);
        Button btn_Commit = findViewById(R.id.buttonCommit);
        btn_Commit.setOnClickListener(this);

        ListUtli listUtil = new ListUtli();
        list_Date = listUtil.getList_Date();
        list_Item = listUtil.getList_Item();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editdate_insert:
                showListPopulWindow(edit_Date, list_Date);
                break;

            case R.id.edititem_insert:
                showListPopulWindow(edit_Item, list_Item);
                break;

            case R.id.buttonCommit:
                studentsInsert();
                break;
        }
    }

    // 显示edittext的下拉列表
    private void showListPopulWindow(final EditText v, final ArrayList<String> list) {

        listPopupWindow.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list));
        listPopupWindow.setAnchorView(v);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            v.setText(list.get(position));
            listPopupWindow.dismiss();
        });
        listPopupWindow.show();
    }

    private void studentsInsert() {
        final String date = edit_Date.getText().toString();
        final String item = edit_Item.getText().toString();
        if (date.equals("") || item.equals("")) {
            Toast.makeText(InsertActivity.this, "请将日期或项目填写完整！", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(InsertActivity.this);
        builder.setMessage("信息无误，确认提交？");
        builder.setPositiveButton("提交", (d, which) -> {
            new CommitThread(date, item).start();
            dialog.setMessage("正在上传，请稍后...");
            dialog.show();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /*
     * 根据当前用户所属班级获取本班所有学生的列表，并初始化为出勤
     */
    class GetStudentsThread extends Thread {

        @Override
        public void run() {
            list_Name = new ArrayList<>();
            list_Status = new ArrayList<>();
            BmobQuery<Students> bq = new BmobQuery<>();
            bq.addWhereEqualTo("ClassOf", mClassOf);
            bq.findObjects(new FindListener<Students>() {

                @Override
                public void done(List<Students> list, BmobException e) {
                    Message msg = Message.obtain();
                    msg.what = Constant.WHAT_INSERT_GETCLASS;
                    if (e == null) {
                        for (Students s : list) {
                            if (!s.getName().equals("管理员")) {
                                list_Name.add(s.getName());
                                list_Status.add("出勤");
                            }
                        }
                        msg.arg1 = 1;
                    } else {
                        msg.arg1 = e.getErrorCode();
                    }
                    mMyHandler.sendMessage(msg);
                }
            });
        }
    }

    // 考勤信息提交上传线程
    class CommitThread extends Thread {

        private String date;
        private String item;

        CommitThread(String date, String item) {
            this.date = date;
            this.item = item;
        }

        @Override
        public void run() {
            List<BmobObject> list_At = new ArrayList<>();

            for (int i = 0; i < list_Name.size(); i++) {
                AttendanceInfo at = new AttendanceInfo();
                at.setName(list_Name.get(i));
                at.setDate(date);
                at.setItem(item);
                at.setClassOf(mClassOf);
                at.setSituation(list_Status.get(i));
                list_At.add(at);
            }

            new BmobBatch().insertBatch(list_At).doBatch(new QueryListListener<BatchResult>() {

                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    Message msg = Message.obtain();
                    msg.what = Constant.WHAT_INSERT_COMMIT;
                    if (e == null) {
                        msg.arg1 = 1;
                    } else {
                        msg.arg1 = 0;
                    }
                    mMyHandler.sendMessage(msg);
                }
            });
        }
    }


    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.WHAT_INSERT_COMMIT:
                    dialog.dismiss();
                    if (msg.arg1 == 1) {
                        edit_Date.setText("");
                        edit_Item.setText("");
                        Remind.showToast(getApplication(), "上传成功！");
                    } else {
                        Remind.showToast(getApplication(), "上传失败！");
                    }
                    break;

                case Constant.WHAT_INSERT_GETCLASS:
                    if (msg.arg1 == 1) {
                        InsertAdapter adapter = new InsertAdapter(InsertActivity.this, list_Name, list_Status);
                        listView_Insert.setAdapter(adapter);
                    } else {
                        Remind.showToast(getApplication(), "获取班级成员失败！" + msg.arg1);
                    }
                    dialog.dismiss();
                    break;
            }
        }
    }
}

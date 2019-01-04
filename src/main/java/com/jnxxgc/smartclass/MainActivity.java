package com.jnxxgc.smartclass;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jnxxgc.smartclass.adapter.QueryAdapter;
import com.jnxxgc.smartclass.table.AttendanceInfo;
import com.jnxxgc.smartclass.table.Students;
import com.jnxxgc.smartclass.util.Constant;
import com.jnxxgc.smartclass.util.Remind;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;


public class MainActivity extends AppCompatActivity implements OnClickListener {

    private final String mName = SmartApplication.getStudents().getName();
    private final String mClass = SmartApplication.getStudents().getClassof();
    private final String mJurisdiction = SmartApplication.getStudents().getJurisdiction();

    private ListView lv_MyAttendance;
    private TextView txt_Count;
    private DrawerLayout mDrawerLayout;
    private MainHandler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //自动检查更新
        BmobUpdateAgent.update(this);

        initView();
    }

    private void initView() {

        //使用toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        //设置toolbar左侧菜单按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu_main);
        }

        mHandler = new MainHandler();

        mDrawerLayout = findViewById(R.id.drawerlayout1);
        lv_MyAttendance = findViewById(R.id.listViewquery);
        txt_Count = findViewById(R.id.textViewqgongji);
        TextView txt_Name = findViewById(R.id.textViewsname);
        TextView txt_Class = findViewById(R.id.textViewsclass);
        Button btn_LogOut = findViewById(R.id.buttonslogout);
        btn_LogOut.setOnClickListener(this);
        TextView ly_KaoQin = findViewById(R.id.linearskaoqin);
        ly_KaoQin.setOnClickListener(this);
        TextView ly_TongJi = findViewById(R.id.linearstongji);
        ly_TongJi.setOnClickListener(this);
        TextView ly_GuanLi = findViewById(R.id.linearsrenyuan);
        ly_GuanLi.setOnClickListener(this);
        TextView ly_MiMa = findViewById(R.id.linearsmima);
        ly_MiMa.setOnClickListener(this);
        TextView ly_BangZhu = findViewById(R.id.linearsbangzhu);
        ly_BangZhu.setOnClickListener(this);
        txt_Name.setText(mName);
        txt_Class.setText(mClass);
    }

    @Override
    protected void onStart() {
        super.onStart();
        txt_Count.setText("正在加载，请稍后...");
        new ReadAttendanceThread().start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(Gravity.START);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // 考勤录入
            case R.id.linearskaoqin:
                if (mJurisdiction.equals("1")) {
                    startActivity(new Intent(MainActivity.this, InsertActivity.class));
                } else {
                    Remind.showToast(SmartApplication.getContext(), "您没有该权限!");
                }
                break;

            // 统计查看
            case R.id.linearstongji:
                if (mJurisdiction.equals("1")) {
                    startActivity(new Intent(MainActivity.this, CheckActivity.class));
                } else {
                    Remind.showToast(SmartApplication.getContext(), "您没有该权限!");
                }
                break;

            // 人员管理
            case R.id.linearsrenyuan:
                if (mJurisdiction.equals("1")) {
                    startActivity(new Intent(MainActivity.this, ManagementActivity.class));
                } else {
                    Remind.showToast(SmartApplication.getContext(), "您没有该权限!");
                }
                break;

            // 修改密码
            case R.id.linearsmima:
                changePassword();
                break;

            // 使用帮助
            case R.id.linearsbangzhu:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;

            // 退出登录
            case R.id.buttonslogout:
                Students.logOut();
                startActivity(new Intent(SmartApplication.getContext(), LogonActivity.class));
                finish();
                break;
        }
    }


    /*
     * 修改密码
     */
    @SuppressLint("InflateParams")
    private void changePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.NewActivityTheme));
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_dialog_changepwd, null);
        final EditText edit_Old;
        final EditText edit_New;
        final EditText edit_New2;
        edit_Old = contentView.findViewById(R.id.editTextold);
        edit_New = contentView.findViewById(R.id.editTextnew);
        edit_New2 = contentView.findViewById(R.id.editTextnew2);
        builder.setTitle("修改密码");
        builder.setView(contentView);
        builder.setPositiveButton("修改", (dialog, which) -> {
            String old = edit_Old.getText().toString();
            String New = edit_New.getText().toString();
            String New2 = edit_New2.getText().toString();
            if (!New.equals(New2)) {
                Remind.showToast(getApplication(), "密码不一致，请重新输入！");
                return;
            }
            BmobUser.updateCurrentUserPassword(old, New, new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Remind.showToast(getApplication(), "修改成功，请使用新密码登录!");
                        BmobUser.logOut();
                        startActivity(new Intent(SmartApplication.getContext(), LogonActivity.class));
                        finish();
                    } else {
                        Remind.showToast(getApplication(), "修改失败！\n" + e.getErrorCode());
                    }
                }
            });
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /*
     * 获取当前登录账户的出勤情况
     */
    class ReadAttendanceThread extends Thread {

        @Override
        public void run() {
            BmobQuery<AttendanceInfo> query = new BmobQuery<>();
            query.addWhereEqualTo("Name", mName);
            query.findObjects(new FindListener<AttendanceInfo>() {

                @Override
                public void done(List<AttendanceInfo> list, BmobException e) {
                    Message msg = Message.obtain();
                    msg.what = Constant.WHAT_MAIN_READMYAT;
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
    }


    @SuppressLint("HandlerLeak")
    class MainHandler extends Handler {

        private int countCq, countQq, countCd, countQj;


        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case Constant.WHAT_MAIN_READMYAT:

                    if (msg.arg1 == 1) {
                        countCd = countCq = countQj = countQq = 0;
                        //将日期、项目、出勤情况分别保存起来
                        List<AttendanceInfo> list_Student = (List<AttendanceInfo>) msg.obj;
                        ArrayList<String> list_Date = new ArrayList<>();
                        ArrayList<String> list_Info = new ArrayList<>();
                        ArrayList<String> list_Item = new ArrayList<>();
                        for (AttendanceInfo info : list_Student) {
                            list_Date.add(info.getDate());
                            list_Item.add(info.getItem());
                            list_Info.add(info.getSituation());

                            //将每种出勤情况分别统计
                            switch (info.getSituation()) {
                                case "出勤":
                                    countQq++;
                                    break;
                                case "缺勤":
                                    countQq++;
                                    break;
                                case "迟到":
                                    countCd++;
                                    break;
                                case "请假":
                                    countQj++;
                                    break;
                            }
                        }

                        QueryAdapter adapter = new QueryAdapter(SmartApplication.getContext(),
                                list_Date, list_Item, list_Info);
                        lv_MyAttendance.setAdapter(adapter);
                        txt_Count.setText("统计：出勤" + countCq + "次");
                        txt_Count.append("缺勤" + countQq + "次，");
                        txt_Count.append("迟到" + countCd + "次，");
                        txt_Count.append("请假" + countQj + "次");
                    } else {
                        BmobException e = (BmobException) msg.obj;
                        txt_Count.setText("获取失败！" + e.getErrorCode());
                    }
                    break;
            }
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Remind.showToast(getApplication(), "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDrawerLayout.closeDrawers();
    }
}

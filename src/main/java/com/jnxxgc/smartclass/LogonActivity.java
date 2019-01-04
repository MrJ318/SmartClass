package com.jnxxgc.smartclass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.jnxxgc.smartclass.table.Students;
import com.jnxxgc.smartclass.util.Constant;
import com.jnxxgc.smartclass.util.Remind;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LogonActivity extends Activity {

    private EditText edit_User, edit_Pwd;
    private CheckBox check_Remember;

    private ProgressDialog dialog;
    private SharedPreferences sp;
    private SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化Bmob
        Bmob.initialize(this, Constant.APPLICATION_ID_D);
        setContentView(R.layout.activity_logon);

        Students students = Students.getCurrentUser(Students.class);
        // 如果有本地缓存则直接进入，否则进行登录
        if (students != null) {
            SmartApplication.setStudents(students);
            startActivity(new Intent(LogonActivity.this, MainActivity.class));
            finish();
        } else {
            initView();
        }
    }

    private void initView() {
        // 4.4以上设置透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        dialog = new ProgressDialog(LogonActivity.this);
        dialog.setCancelable(false);

        edit_User = findViewById(R.id.editUser);
        edit_Pwd = findViewById(R.id.editPwd);
        check_Remember = findViewById(R.id.checkBox1);
        Button button = findViewById(R.id.buttonLogon);
        button.setOnClickListener(v -> logOn());

        readUserInfo();
    }

    private void readUserInfo() {

        // 初始化SharedPreferences存储
        sp = getSharedPreferences("UserInfo", MODE_PRIVATE);

        String name = sp.getString("UserName", "NULL");
        String password = sp.getString("Password", "NULL");
        boolean isCheck = sp.getBoolean("ck", false);

        if (!name.equals("NULL")) {
            edit_User.setText(name);
        }
        if (!password.equals("NULL")) {
            edit_Pwd.setText(password);
        }

        check_Remember.setChecked(isCheck);
    }

    private void logOn() {
        ed = sp.edit();
        final String name = edit_User.getText().toString();
        String password = edit_Pwd.getText().toString();
        ed.putString("UserName", name);
        if (check_Remember.isChecked()) {
            ed.putString("Password", password);
            ed.putBoolean("ck", true);
        } else {
            ed.putString("Password", "");
            ed.putBoolean("ck", false);
        }
        if (name.equals("") | password.equals("")) {
            Remind.showToast(getApplication(), "用户名和密码不能为空！");
            return;
        }
        dialog.setMessage("正在登陆，请稍后...");
        dialog.show();
        Students student = new Students();
        student.setUsername(name);
        student.setPassword(password);
        student.login(new SaveListener<Students>() {

            @Override
            public void done(Students user, BmobException e) {
                if (e == null) {
                    SmartApplication.setStudents(user);
                    Remind.showToast(getApplication(), "登陆成功！");
                    startActivity(new Intent(LogonActivity.this, MainActivity.class));
                    finish();
                } else if (e.getErrorCode() == 101) {
                    Remind.showToast(getApplication(), "用户名或密码错误！");
                } else if (e.getErrorCode() == 9016) {
                    Remind.showToast(getApplication(), "无网络连接，请检查您的手机网络!");
                } else {
                    Remind.showToast(getApplication(), "未知错误!\n" + e.getErrorCode());
                }
                dialog.dismiss();
                ed.apply();
            }
        });
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

}

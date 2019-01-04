package com.jnxxgc.smartclass;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.github.promeg.pinyinhelper.Pinyin;
import com.jnxxgc.smartclass.table.Students;
import com.jnxxgc.smartclass.util.Constant;
import com.jnxxgc.smartclass.util.Remind;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class PersonalActivity extends AppCompatActivity {

    private EditText edit_Name, edit_Sex, edit_Class, edit_UserName;
    private EditText edit_Pwd, edit_Reg, edit_Birth, edit_Jur;

    private String str_Name, str_Sex, str_Class;
    private String str_Pwd, str_Reg, str_Birth, str_Jur;
    private Students mStudents;



    private Context context = PersonalActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        initView();

        switch (getIntent().getIntExtra("index", 0)) {
            case Constant.INTENT_EXTRA_SEE:// 查看
                seeInformation();
                break;
            case Constant.INTENT_EXTRA_ADD:// 添加
                edit_UserName.setEnabled(false);
                edit_UserName.setHint("系统自动生成，无需填写");
                break;
        }
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.toolbar_person);
        setSupportActionBar(toolbar);
        //设置toolbar左侧菜单按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        edit_Name = findViewById(R.id.editTextpname);
        edit_Sex = findViewById(R.id.editTextpsex);
        edit_Class = findViewById(R.id.editTextpclass);
        edit_UserName = findViewById(R.id.editTextpusername);
        edit_Pwd = findViewById(R.id.editTextppwd);
        edit_Reg = findViewById(R.id.editTextpreg);
        edit_Birth = findViewById(R.id.editTextpbirth);
        edit_Jur = findViewById(R.id.editTextpjur);

        mStudents = (Students) getIntent().getSerializableExtra("id");
    }

    @SuppressLint("SetTextI18n")
    private void seeInformation() {


        edit_Name.setText("姓名："+mStudents.getName());
        edit_Sex.setText("性别："+mStudents.getSex());
        edit_Class.setText("班级："+mStudents.getClassof());
        edit_UserName.setText("用户名："+mStudents.getUsername());
        edit_Pwd.setText("密码：******");
        edit_Reg.setText("学籍号："+mStudents.getReg_Number());
        edit_Birth.setText("出生日期："+mStudents.getBirth_Date());
        edit_Jur.setText("权限："+mStudents.getJurisdiction());

        edit_Name.setEnabled(false);
        edit_Sex.setEnabled(false);
        edit_Class.setEnabled(false);
        edit_UserName.setEnabled(false);
        edit_Pwd.setEnabled(false);
        edit_Reg.setEnabled(false);
        edit_Birth.setEnabled(false);
        edit_Jur.setEnabled(false);
    }

    private void initData() {
        str_Name = edit_Name.getText().toString();
        str_Sex = edit_Sex.getText().toString();
        str_Class = edit_Class.getText().toString();
        str_Pwd = edit_Pwd.getText().toString();
        str_Reg = edit_Reg.getText().toString();
        str_Birth = edit_Birth.getText().toString();
        str_Jur = edit_Jur.getText().toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getIntExtra("index", 0) == Constant.INTENT_EXTRA_ADD) {
            getMenuInflater().inflate(R.menu.personal, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_commit:
                insertPerson();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertPerson() {
        initData();
        if (str_Name.equals("") | str_Sex.equals("") | str_Class.equals("") | str_Pwd.equals("") | str_Reg.equals("")
                | str_Birth.equals("") | str_Jur.equals("")) {
            Remind.showToast(getApplication(), "请将信息填写完整!");
            return;
        }
        char[] arr = str_Name.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : arr) {
            String pyin = Pinyin.toPinyin(c);
            sb.append(pyin);
        }
        sb.append(str_Reg.substring(str_Reg.length() - 4));
        Students insertStudent = new Students();
        insertStudent.setName(str_Name);
        insertStudent.setSex(str_Sex);
        insertStudent.setClassof(str_Class);
        insertStudent.setUsername(sb.toString());
        insertStudent.setPassword(str_Pwd);
        insertStudent.setReg_Number(str_Reg);
        insertStudent.setBirth_Date(str_Birth);
        insertStudent.setJurisdiction(str_Jur);
        insertStudent.signUp(new SaveListener<Students>() {

            @Override
            public void done(Students st, BmobException e) {
                if (e == null) {
                    showSuccessDialog(st);
                } else {
                    Remind.showToast(getApplication(), "上传失败！\n" + e.getErrorCode());
                }
            }
        });
    }

    private void showSuccessDialog(Students student) {
        edit_Birth.setText("");
        edit_Class.setText("");
        edit_Jur.setText("");
        edit_Name.setText("");
        edit_Pwd.setText("");
        edit_Reg.setText("");
        edit_Sex.setText("");
        final String userName = student.getUsername();
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("保存成功!");
        dialog.setMessage("您的用户名为：" + userName);
        dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton("复制", (dialog1, which) -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipdate = ClipData.newPlainText("text", userName);
            if (cm!=null){
                cm.setPrimaryClip(clipdate);
            }
            Remind.showToast(getApplication(), "用户名已复制到剪贴板!");
        });
        dialog.show();
    }


}

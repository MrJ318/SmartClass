package com.jnxxgc.smartclass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jnxxgc.smartclass.table.Students;
import com.jnxxgc.smartclass.util.Constant;
import com.jnxxgc.smartclass.util.FileReadAndWrite;
import com.jnxxgc.smartclass.util.Remind;
import com.jnxxgc.smartclass.util.StoragePath;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class ManagementActivity extends AppCompatActivity {

    private ListView lv_AllPerson;

    private ArrayList<Students> list_Student;
    private ArrayList<Students> list_QueryResult;
    private ProgressDialog upLoadDialog;

    private String excelPath;
    private MyHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryAllStudents();
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.toolbar_personmanage);
        setSupportActionBar(toolbar);

        lv_AllPerson = findViewById(R.id.listViewperson);

        mHandler = new MyHandler();

        upLoadDialog = new ProgressDialog(this);
        upLoadDialog.setCancelable(false);

        lv_AllPerson.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(SmartApplication.getContext(), PersonalActivity.class);
            i.putExtra("index", Constant.INTENT_EXTRA_SEE);
            i.putExtra("id", list_QueryResult.get(position));
            startActivity(i);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.management, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_query:
                queryAllStudents();
                break;

            case R.id.action_add:
                Intent i = new Intent(SmartApplication.getContext(), PersonalActivity.class);
                i.putExtra("index", Constant.INTENT_EXTRA_ADD);
                startActivity(i);
                break;

            case R.id.action_import:
                // 申请权限
                if (ContextCompat.checkSelfPermission(ManagementActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ManagementActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    getPersonFile();
                }
                break;
        }
        return true;
    }

    /*
     * 查询全部
     */
    private void queryAllStudents() {
        ProgressDialog queryDialog = new ProgressDialog(this);
        queryDialog.setMessage("正在查询，请稍后...");
        queryDialog.show();

        list_QueryResult = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        BmobQuery<Students> bq = new BmobQuery<>();
        bq.addWhereEqualTo("ClassOf", SmartApplication.getStudents().getClassof());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ManagementActivity.this, android.R.layout.simple_list_item_1, names);

        bq.findObjects(new FindListener<Students>() {

            @Override
            public void done(List<Students> list, BmobException e) {

                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        String name = list.get(i).getName();
                        if (!name.equals("") & !name.equals("管理员")) {
                            names.add(name);
                            list_QueryResult.add(list.get(i));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    lv_AllPerson.setAdapter(adapter);
                } else {
                    Remind.showToast(ManagementActivity.this,
                            "未查询到数据！\n" + e.getMessage());
                }
                queryDialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPersonFile();
            } else {
                Toast.makeText(ManagementActivity.this, "权限被拒绝！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * 调用系统文件管理，选择文件,返回文件路径
     */
    private void getPersonFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), Constant.FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SmartApplication.getContext(), "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(SmartApplication.getContext(), "文件选择失败！", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == Constant.FILE_SELECT_CODE) {

            Uri uri = data.getData();
            if (uri == null) {
                Remind.showToast(SmartApplication.getContext(), "url错误");
                return;
            }
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                // 使用第三方应用打开
                excelPath = uri.getPath();
                // 调用读取excel文件方法
                list_Student = FileReadAndWrite.readExcel(SmartApplication.getContext(), excelPath);

                if (list_Student != null) {
                    // 如果读取文件正确
                    showReadInfo();
                }
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                // 4.4以后
                // path = getPath(this, uri);
                excelPath = StoragePath.getPath(this, uri);
            } else {
                // 4.4以下下系统调用方法
                // path = getRealPathFromURI(uri);
                excelPath = StoragePath.getRealPathFromURI(this, uri);
            }
            // 调用读取excel文件方法
            list_Student = FileReadAndWrite.readExcel(SmartApplication.getContext(), excelPath);

            if (list_Student != null) {// 如果读取文件正确
                String myClass = SmartApplication.getStudents().getClassof();
                String class1 = list_Student.get(1).getClassof();
                String class2 = list_Student.get(list_Student.size() / 2).getClassof();
                String class3 = list_Student.get(list_Student.size() / 4).getClassof();
                if (class1.equals(myClass) | class2.equals(myClass) | class3.equals(myClass)) {
                    showReadInfo();
                } else {
                    Remind.showToast(SmartApplication.getContext(), "当前用户与要导入的班级不符，请检查!");
                }
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * 确认上传
     */
    private void showReadInfo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ManagementActivity.this);
        builder.setMessage("共读取到" + list_Student.size() + "个学生信息，是否提交？");
        builder.setPositiveButton("确定", (dialog, which) -> {
            new LoginThread().start();
            upLoadDialog.setMessage("正在上传，请稍后...");
            upLoadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            upLoadDialog.setMax(list_Student.size());
            upLoadDialog.show();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }


    class LoginThread extends Thread {

        private int count_Failed = 0;
        private int progress = 0;

        @Override
        public void run() {
            for (final Students s : list_Student) {
                s.signUp(new SaveListener<Students>() {

                    @Override
                    public void done(Students st, BmobException e) {
                        if (e == null) {
                            FileReadAndWrite.writeUpLoadResult(excelPath, Constant.RESULT_UPLOAD_SUCCESS,
                                    s.getName() + ":" + s.getUsername() + "\n");
                        } else {
                            count_Failed++;
                            FileReadAndWrite.writeUpLoadResult(excelPath, Constant.RESULT_UPLOAD_FAILED,
                                    s.getName() + ":" + e.getErrorCode() + "--" + e.getMessage() + "\n");
                        }
                        progress++;
                        Message msg = Message.obtain();
                        msg.what = Constant.WHAT_LOGIN;
                        msg.arg1 = progress;
                        msg.arg2 = count_Failed;
                        mHandler.sendMessage(msg);
                    }
                });
                try {
                    sleep(200);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Constant.WHAT_LOGIN) {
                int progress = msg.arg1;
                int faild = msg.arg2;
                upLoadDialog.setProgress(progress);
                if (progress == list_Student.size()) {
                    upLoadDialog.dismiss();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ManagementActivity.this);
                    dialog.setTitle("上传完成!");
                    dialog.setMessage("上传成功：" + (progress - faild) + "个\n上传失败：" + faild
                            + "个\n相应的用户名已存储到与人员表同一目录下的<用户名.txt>文件中。" + "\n详细的错误信息已存储到与人员表同一目录下的<错误信息.txt>文件中。");
                    dialog.setNegativeButton("OK", null);
                    dialog.show();
                    BmobUser.logOut();
                }
            }
        }
    }
}

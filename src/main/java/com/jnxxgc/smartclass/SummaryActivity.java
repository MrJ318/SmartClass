package com.jnxxgc.smartclass;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;

import com.jnxxgc.smartclass.table.AttendanceInfo;
import com.jnxxgc.smartclass.util.Constant;
import com.jnxxgc.smartclass.util.MyDate;
import com.jnxxgc.smartclass.util.Remind;
import com.jnxxgc.smartclass.util.RequestPermissions;
import com.jnxxgc.smartclass.thread.SummaryThread;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SummaryActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static WebView web_Summary;

    private static boolean isLoad = false;
    private static ProgressDialog dialog;
    private static Handler handler;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置安卓5.0以上的截图方式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        setContentView(R.layout.activity_summary);

        initWebView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getInternetData(getIntent().getStringExtra("ClassOf"));
    }

    private void initWebView() {

        Toolbar toolbar = findViewById(R.id.toolbar_sum);
        setSupportActionBar(toolbar);

        web_Summary = findViewById(R.id.webViewsummary);

        //********************* 设置高度 **********************************
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        LayoutParams lp = web_Summary.getLayoutParams();
        lp.height = width;
        web_Summary.setLayoutParams(lp);

        dialog = new ProgressDialog(SummaryActivity.this);
        dialog.setMessage("正在加载，请稍后...");

        handler = new MyHandler(this);
    }

    // 创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.summary, menu);
        return true;
    }

    // 菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (RequestPermissions.getWRITE_EXTERNAL_STORAGE(SummaryActivity.this)) {
                    savePic();
                }
                break;

            case R.id.action_fen:
                shareTo();
                break;

            case R.id.action_redraw:
                getInternetData(getIntent().getStringExtra("ClassOf"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 保存图片
    public void savePic() {

        if (!isLoad) {
            Remind.showToast(getApplication(), "未找到数据，无法保存！");
            return;
        }
        @SuppressWarnings("deprecation")
        Picture picture = web_Summary.capturePicture();
        Bitmap bmp = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmp);
        picture.draw(c);

        File f = new File(Environment.getExternalStorageDirectory() + "/Pictures");
        if (!f.exists()) {
            f.mkdirs();
        }
        File myCaptureFile = new File(f, System.currentTimeMillis() + ".jpg");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            Remind.showToast(getApplication(), "图片已保存到：" + myCaptureFile.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Remind.showToast(getApplication(), "保存出错！");
        } catch (IOException e) {
            e.printStackTrace();
            Remind.showToast(getApplication(), "保存出错！");
        }
    }

    // 分享图片
    private void shareTo() {
        if (!isLoad) {
            Remind.showToast(getApplication(), "未找到数据，无法分享！");
            return;
        }

        @SuppressWarnings("deprecation")
        Picture picture = web_Summary.capturePicture();
        Bitmap bmp = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmp);
        picture.draw(c);

        File cache = new File(Environment.getExternalStorageDirectory(), "/Android/test.j");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cache));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

            Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "/Android/test.j"));
            Intent imageIntent = new Intent();
            imageIntent.setAction(Intent.ACTION_SEND);
            imageIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            imageIntent.setType("image/*");
            startActivity(Intent.createChooser(imageIntent, "分享"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Remind.showToast(getApplication(), "保存出错！");
        } catch (IOException e) {
            e.printStackTrace();
            Remind.showToast(getApplication(), "保存出错！");
        }
    }

    @SuppressLint("NewApi")
    private void getInternetData(String classof) {

        dialog.show();

        new Thread() {
            public void run() {
                BmobQuery<AttendanceInfo> bq = new BmobQuery<>();
                bq.addWhereContainedIn("Date", MyDate.getDates(Calendar.getInstance()));
                bq.addWhereEqualTo("ClassOf", classof);
                bq.setLimit(500);
                bq.findObjects(new FindListener<AttendanceInfo>() {

                    @Override
                    public void done(List<AttendanceInfo> list, BmobException e) {
                        Message msg = Message.obtain();
                        msg.what = Constant.WHAT_SUNMMARY;
                        if (e == null) {
                            msg.arg1 = 1;
                            msg.obj = list;
                        } else {
                            msg.arg1 = 0;
                            msg.obj = e;
                        }
                        handler.sendMessage(msg);
                    }
                });

            }
        }.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constant.REQUEST_PERMISSION_WRITE & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            savePic();
        } else {
            Remind.showToast(getApplication(), "权限被拒绝！");
        }
    }

    static class MyHandler extends Handler {

        // 建立弱引用
        private WeakReference<SummaryActivity> refrence;
        private SummaryActivity context;

        private List<AttendanceInfo> list;

        MyHandler(SummaryActivity m) {
            refrence = new WeakReference<>(m);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            // 通过WeakReference对象获取SummaryActivity对象
            context = refrence.get();

            switch (msg.what) {
                // 从云数据库中查询数据完成
                case Constant.WHAT_SUNMMARY:
                    if (msg.arg1 == 1) {
                        list = (List<AttendanceInfo>) msg.obj;
                        if (list.size() == 0) {
                            dialog.dismiss();
                            Remind.showToast(context, "未获取到相关信息！");
                            return;
                        }
                        // 若果查询到的数据不为空，则开始解析数据
                        new SummaryThread(context, MyDate.getDates(Calendar.getInstance()), list, handler).start();
                    } else {
                        dialog.dismiss();
                        BmobException e = (BmobException) msg.obj;
                        Remind.showToast(context, "查询时出错:" + e.getErrorCode() + e.getMessage());
                    }
                    break;

                // 生成HTML文件
                case Constant.WHAT_SAVE:
                    web_Summary.loadUrl("file:///" + context.getCacheDir() + "/table.html");
                    isLoad = true;
                    dialog.dismiss();
                    break;
            }
            super.handleMessage(msg);
        }
    }
}

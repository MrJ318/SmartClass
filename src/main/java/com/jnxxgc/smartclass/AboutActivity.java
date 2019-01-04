package com.jnxxgc.smartclass;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateStatus;

public class AboutActivity extends AppCompatActivity implements OnClickListener {

    private TextView txt_Version;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initView();

        try {
            PackageInfo pack = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            txt_Version.setText("V" + pack.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void initView() {

        Toolbar toolbar = findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar);

        TextView txt_App = findViewById(R.id.textViewupdataapp);
        txt_App.setOnClickListener(this);
        TextView txt_Help = findViewById(R.id.textViewhelp);
        txt_Help.setOnClickListener(this);
        TextView txt_Copyright = findViewById(R.id.textViewcopyright);
        txt_Copyright.setOnClickListener(this);
        txt_Version = findViewById(R.id.textViewversion);
        TextView txt_QQ = findViewById(R.id.textViewQQ);
        txt_QQ.setOnClickListener(this);
        TextView txt_Email = findViewById(R.id.textViewEmail);
        txt_Email.setOnClickListener(this);
    }
    

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(AboutActivity.this, HelpActivity.class);
        switch (v.getId()) {
            case R.id.textViewupdataapp:
                upDataApp();
                break;

            case R.id.textViewhelp:
                intent.putExtra("TAG", "help");
                startActivity(intent);
                break;

            case R.id.textViewcopyright:
                intent.putExtra("TAG", "copyright");
                startActivity(intent);
                break;
            case R.id.textViewQQ:
                try {
                    String url11 = "mqqwpa://im/chat?chat_type=wpa&uin=1599937006&version=1";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url11)));
                } catch (Exception e) {
                    Toast.makeText(AboutActivity.this, "您的手机未安装QQ!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.textViewEmail:
                Uri uri = Uri.parse("mailto:Jevon3@163.com");
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(it);
                break;
        }
    }

    private void upDataApp() {

        BmobUpdateAgent.setUpdateListener((updateStatus, updateInfo) -> {
            if (updateStatus == UpdateStatus.No) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(AboutActivity.this);
                dialog.setMessage("当前已是最新版本！");
                dialog.setPositiveButton("确定", null);
                dialog.show();
            }
        });

        BmobUpdateAgent.forceUpdate(this);
    }
}

package com.jnxxgc.smartclass;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = findViewById(R.id.toolbar_help);
        setSupportActionBar(toolbar);

        WebView web = findViewById(R.id.webView1);
        web.clearCache(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getStringExtra("TAG").equals("help")) {
            getSupportActionBar().setTitle("使用帮助");
            web.loadUrl("file:///android_asset/help.html");
        } else if (getIntent().getStringExtra("TAG").equals("copyright")) {
            getSupportActionBar().setTitle("版权声明");
            web.loadUrl("file:///android_asset/copy.html");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

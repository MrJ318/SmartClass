package com.jnxxgc.smartclass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.ListView;

import com.jnxxgc.smartclass.adapter.CheckAdapter;
import com.jnxxgc.smartclass.table.AttendanceInfo;
import com.jnxxgc.smartclass.util.ListUtli;
import com.jnxxgc.smartclass.util.Remind;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CheckActivity extends AppCompatActivity implements OnClickListener {

    private EditText edit_Date, edit_Item;
    private ListView listView_Check;

    private final String ClassOf = SmartApplication.getStudents().getClassof();
    private ArrayList<String> list_Date;
    private ArrayList<String> list_Item;

    private ListPopupWindow listPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        initView();
    }

    private void initView() {

        Toolbar toolbar=findViewById(R.id.toolbar_check);
        setSupportActionBar(toolbar);

        listPopupWindow = new ListPopupWindow(this);
        listView_Check = findViewById(R.id.listViewcheck);
        edit_Date = findViewById(R.id.editdate_check);
        edit_Date.setOnClickListener(this);
        edit_Item = findViewById(R.id.edititem_check);
        edit_Item.setOnClickListener(this);

        ListUtli listUtil = new ListUtli();
        list_Date = listUtil.getList_Date();
        list_Item = listUtil.getList_Item();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editdate_check:
                showListPopulWindow(edit_Date, list_Date);
                break;

            case R.id.edititem_check:
                showListPopulWindow(edit_Item, list_Item);
                break;
        }
    }

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

    private void studentsCheck() {
        String date = edit_Date.getText().toString();
        String item = edit_Item.getText().toString();
        if (date.equals("") || item.equals("")) {
            Remind.showToast(getApplication(), "请选择要查询的日期或项目!");
            return;
        }
        BmobQuery<AttendanceInfo> query = new BmobQuery<>();
        query.addWhereEqualTo("Date", date);
        query.addWhereEqualTo("Item", item);
        query.addWhereEqualTo("ClassOf", ClassOf);
        query.findObjects(new FindListener<AttendanceInfo>() {

            @Override
            public void done(List<AttendanceInfo> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        getInformation(list);
                    } else {
                        Remind.showToast(getApplication(), "没有找到纪录！");
                    }
                } else {
                    Remind.showToast(getApplication(), "没有找到纪录！");
                }
            }
        });
    }

    private void getInformation(List<AttendanceInfo> list) {
        ArrayList<String> list_Name = new ArrayList<>();
        ArrayList<String> list_Status = new ArrayList<>();
        for (AttendanceInfo a : list) {
            list_Name.add(a.getName());
            list_Status.add(a.getSituation());
        }
        CheckAdapter adapter = new CheckAdapter(CheckActivity.this, list_Name, list_Status);
        listView_Check.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.check, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_query:
                studentsCheck();
                break;

            case R.id.action_huizong:
                Intent intent = new Intent(CheckActivity.this, SummaryActivity.class);
                intent.putExtra("ClassOf", ClassOf);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

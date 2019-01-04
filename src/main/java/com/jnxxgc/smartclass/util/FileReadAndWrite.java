package com.jnxxgc.smartclass.util;

import android.content.Context;
import android.widget.Toast;

import com.github.promeg.pinyinhelper.Pinyin;
import com.jnxxgc.smartclass.table.Students;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class FileReadAndWrite {

    public static void writeUpLoadResult(String path, String name, String msg) {
        File file = new File(path);
        String parentPath = file.getParentFile().getAbsolutePath();
        File reslutFile = new File(parentPath, name + ".txt");
        try {
            FileOutputStream fos = new FileOutputStream(reslutFile, true);
            fos.write(msg.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Students> readExcel(Context context, String path) {
        File f = new File(path);
        Workbook wbook;
        try {
            wbook = Workbook.getWorkbook(f);
        } catch (BiffException e) {
            e.printStackTrace();
            Toast.makeText(context, "你选择的文件不符合格式，请检查！", Toast.LENGTH_SHORT).show();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "读取文件错误！", Toast.LENGTH_SHORT).show();
            return null;
        }
        Sheet sheet = wbook.getSheet(0);
        int rows = sheet.getRows();// 获取表的行数
        Jlog.d(Jlog.TAG, "读取Excel完成，读取到行数:" + rows);
        ArrayList<Students> list = new ArrayList();
        for (int i = 1; i < rows; i++) {
            Students stu = new Students();
            String name = sheet.getCell(0, i).getContents();
            String number = sheet.getCell(1, i).getContents();
            String pwd = sheet.getCell(5, i).getContents();
            if (name.equals("") | number.equals("") | pwd.equals("")) {
                continue;
            }
            stu.setName(name);
            stu.setReg_Number(number);
            stu.setSex(sheet.getCell(2, i).getContents());
            stu.setBirth_Date(sheet.getCell(3, i).getContents());
            stu.setClassof(sheet.getCell(4, i).getContents());
            // *************读取姓名获取拼音，转换成用户名 ***********
            char[] arr = name.toCharArray();
            StringBuilder sb = new StringBuilder();
            for (char c : arr) {
                String pyin = Pinyin.toPinyin(c);
                sb.append(pyin);
            }
            sb.append(number.substring(number.length() - 4));
            // *************读取姓名获取拼音，转换成用户名 end***********
            stu.setUsername(sb.toString());
            stu.setPassword(pwd);
            stu.setJurisdiction(sheet.getCell(6, i).getContents());
            list.add(stu);
        }
        return list;
    }

}

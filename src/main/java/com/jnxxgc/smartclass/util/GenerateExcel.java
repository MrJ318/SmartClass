package com.jnxxgc.smartclass.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class GenerateExcel {

    private static String stuclass = "class";
    private static String startTime = "**";
    private static String endTime = "**";
    private static List<String> items = null;
    private static File f = new File(Environment.getExternalStorageDirectory(), "test.xls");
    private static WritableWorkbook workbook = null;


    public static void setEndTime(String endTime) {
        GenerateExcel.endTime = endTime;
    }

    public static void setItems(List<String> items) {
        GenerateExcel.items = items;
    }

    public static void setStartTime(String startTime) {
        GenerateExcel.startTime = startTime;
    }

    public static void setStuclass(String stuclass) {
        GenerateExcel.stuclass = stuclass;
    }

    /*
     * 创建表头
     * */
    private static void makeHead() {

        //创建工作簿
        try {
            workbook = Workbook.createWorkbook(f);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Mr.J", "创建");
        }

        //创建sheet1表
        WritableSheet sheet = workbook.createSheet("sheet1", 0);
        try {
            //合并单元格-大标题
            sheet.mergeCells(0, 0, 5 * items.size(), 0);
            sheet.mergeCells(0, 1, 0, 2);

            //设置大标题字体和对齐方式
            WritableFont font1 = new WritableFont(WritableFont.ARIAL, 20, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
            WritableCellFormat format = new WritableCellFormat(font1);
            format.setAlignment(Alignment.CENTRE);

            //添加标题文字
            Label label = new Label(0, 0, stuclass + "  " + startTime + "--" + endTime + "考勤表", format);
            sheet.addCell(label);

        } catch (WriteException e) {
            e.printStackTrace();
        }

        int index = 1;
        for (int i = 0; i < items.size(); i++) {

            try {
                //合并单元格-二级标题
                sheet.mergeCells(index, 1, (index + 4), 1);

                //添加二级标题，设置对齐方式
                WritableCellFormat format = new WritableCellFormat();
                format.setAlignment(Alignment.CENTRE);
                sheet.addCell(new Label(index, 1, items.get(i), format));
                Log.d("Mr.J", "**" + i + items.get(i));

                for (int j = index; j < (index + 5); j++) {
                    String week = "";
                    switch (j % 5) {
                        case 1:
                            week = "星期一";
                            break;

                        case 2:
                            week = "星期二";
                            break;

                        case 3:
                            week = "星期三";
                            break;

                        case 4:
                            week = "星期四";
                            break;

                        case 0:
                            week = "星期五";
                            break;
                    }
                    Label label1 = new Label(j, 2, week);
                    sheet.addCell(label1);
                }
            } catch (WriteException e) {
                e.printStackTrace();
            }
            index += 5;
        }

    }

    public static void saveExcel() {
        makeHead();


        try {
            workbook.write();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }


}

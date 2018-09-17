package com.huawei.spring.core.implement.SQLScan.util;

import com.huawei.spring.catalogue.SQLScan.ScanResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

  public static SXSSFWorkbook getWorkBook(List<ScanResult> results) {
    SXSSFWorkbook wb = new SXSSFWorkbook(1000);
    SXSSFSheet sheet = wb.createSheet("sheet1");
    setWidth(sheet);
    SXSSFRow row = sheet.createRow(0);
    row.setHeightInPoints(40);
    //CellStyle style = wb.createCellStyle();
    CellStyle style = getCellStyle(wb);
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    style.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
    Map<Integer, String> columnMap = resultColumnMap();
    SXSSFCell cell = null;
    for (Map.Entry<Integer, String> entry : columnMap.entrySet()){
      cell = row.createCell(entry.getKey());
      cell.setCellValue(entry.getValue());
      cell.setCellStyle(style);
    }
    for (int i = 0; i < results.size(); i++) {
      row = sheet.createRow(i + 1);
      addResultCell(row, results.get(i), wb);
    }
    return wb;
  }

  private static Map<Integer, String> resultColumnMap() {
    Map<Integer, String> map = new HashMap<>();
    map.put(0, "ID");
    map.put(1, "结果名称");
    map.put(2, "执行状态");
    map.put(3, "执行结果");
    map.put(4, "规则等级");
    map.put(5, "提示信息");
    map.put(6, "起始行");
    map.put(7, "结束行");
    map.put(8, "业务名称");
    map.put(9, "创建时间");
    map.put(10, "SQL");
    map.put(11, "脚本名称");
    map.put(12, "操作类型");
    map.put(13, "对应规则");
    map.put(14, "操作人");
    return map;
  }

  private static void addResultCell(SXSSFRow row, ScanResult result, SXSSFWorkbook wb) {
    CellStyle style = getCellStyle(wb);
    int height = getHeight(result.getSqlString());
    row.setHeightInPoints(height);
    SXSSFCell cell = row.createCell(0);
    cell.setCellValue(result.getId());
    cell.setCellStyle(style);
    cell = row.createCell(1);
    cell.setCellValue(result.getName());
    cell.setCellStyle(style);
    cell = row.createCell(2);
    cell.setCellValue(result.getStatus());
    cell.setCellStyle(style);
    cell = row.createCell(3);
    cell.setCellValue(result.getHead());
    cell.setCellStyle(style);
    cell = row.createCell(4);
    cell.setCellValue(result.getLevel());
    cell.setCellStyle(style);
    cell = row.createCell(5);
    cell.setCellValue(result.getMessage());
    cell.setCellStyle(style);
    cell = row.createCell(6);
    cell.setCellValue(result.getStartLineNum());
    cell.setCellStyle(style);
    cell = row.createCell(7);
    cell.setCellValue(result.getEndLineNum());
    cell.setCellStyle(style);
    cell = row.createCell(8);
    cell.setCellValue(result.getBusiness());
    cell.setCellStyle(style);
    cell = row.createCell(9);
    cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(result.getCreated())); //todo format
    cell.setCellStyle(style);
    cell = row.createCell(10);
    cell.setCellValue(result.getSqlString()); //todo format
    cell.setCellStyle(style);
    cell = row.createCell(11);
    cell.setCellStyle(style);
    cell.setCellValue(result.getScriptName());
    cell.setCellStyle(style);
    cell = row.createCell(12);
    cell.setCellValue(result.getOperType());
    cell.setCellStyle(style);
    cell = row.createCell(13);
    cell.setCellValue(result.getRuleName());
    cell.setCellStyle(style);
    cell = row.createCell(14);
    cell.setCellValue(result.getOperator());
    cell.setCellStyle(style);
  }

  private static void setWidth(SXSSFSheet sheet) {
    sheet.setColumnWidth(0, 34 * 256);
    sheet.setColumnWidth(1, 30 * 256);
    sheet.setColumnWidth(5, 48 * 256);
    sheet.setColumnWidth(8, 16 * 256);
    sheet.setColumnWidth(9, 21 * 256);
    sheet.setColumnWidth(10, 120 * 256);
    sheet.setColumnWidth(11, 20 * 256);
    sheet.setColumnWidth(13, 24 * 256);
    sheet.setColumnWidth(14, 16 * 256);
  }

  private static CellStyle getCellStyle (SXSSFWorkbook wb) {
    CellStyle style = wb.createCellStyle();
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    style.setAlignment(HorizontalAlignment.LEFT);
    style.setWrapText(true);
    return style;
  }

  private static int getHeight(String s) {
    int h = 0;
    if (null == s || "".equals(s)) {
      h = 20;
    } else {
      String[] split = s.split("\n");
      if (split.length * 15 >= 400) {
        h = 400;
      } else {
        h = split.length * 20;
      }
    }
    return h;
  }
}

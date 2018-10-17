//package com.huawei.spring.core.implement.SQLScan.util;
//
//import com.znlhzl.bi.entity.vo.ClaimReportVo;
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.IndexedColors;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.xssf.streaming.SXSSFCell;
//import org.apache.poi.xssf.streaming.SXSSFRow;
//import org.apache.poi.xssf.streaming.SXSSFSheet;
//import org.apache.poi.xssf.streaming.SXSSFWorkbook;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Field;
//import java.net.URLEncoder;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//
//public class ExcelUtil2 {
//    public static SXSSFWorkbook getWorkBook(List<?> objects, Class c) throws IllegalAccessException {
//        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
//        SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("sheet1");
//        SXSSFRow row = (SXSSFRow) sheet.createRow(0);
//        row.setHeightInPoints(40);
//        CellStyle style = getCellStyle(wb);
//        style.setAlignment(CellStyle.ALIGN_CENTER);
//        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
//        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
//        int n = setCellTitle(c, row, style);
//        for (int i = 0; i < objects.size(); i++) {
//            row = (SXSSFRow) sheet.createRow(i + 1);
//            useReflectToGetValue(row, objects.get(i), wb);
//        }
//        autoSizeColumns(sheet, n);
//        return wb;
//    }
//
//    private static int setCellTitle(Class c, SXSSFRow row, CellStyle style) {
//        // Map<Integer, String> columnMap = (Map<Integer, String>) c.getMethod("getFieldsAlias").invoke(c.newInstance());
//        SXSSFCell cell;
//        Field[] fields = c.getDeclaredFields();
//        int n = 0;
//        for (Field f : fields) {
//            f.setAccessible(true);
//            ExcelTitle title = f.getAnnotation(ExcelTitle.class);
//            if (null != title) {
//                cell = (SXSSFCell) row.createCell(n);
//                cell.setCellValue(title.value());
//                cell.setCellStyle(style);
//                n++;
//            }
//        }
//        return n + 1;
//    }
//
//    private static void useReflectToGetValue(SXSSFRow row, Object object, SXSSFWorkbook wb) throws IllegalAccessException {
//        Class<?> aClass = object.getClass();
//        Field[] fields = aClass.getDeclaredFields();
//        CellStyle style = getCellStyle(wb);
//        SXSSFCell cell;
//        int n = 0;
//        for (Field f : fields) {
//            f.setAccessible(true);
//            ExcelTitle title = f.getAnnotation(ExcelTitle.class);
//            if (null != title) {
//                cell = (SXSSFCell) row.createCell(n);
//                Object value = f.get(object);
//                if (flagForClaim(object, f.getName())) {
//                    value = ClaimReportVo.statusMap().get(String.valueOf(value));
//                }
//                if (value instanceof Date) {
//                    cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value));
//                } else if (value == null) {
//                    cell.setCellValue("");
//                } else {
//                    cell.setCellValue(String.valueOf(value));
//                }
//                cell.setCellStyle(style);
//                n++;
//            }
//        }
//    }
//
//    private static boolean flagForClaim(Object o, String name) {
//        return o instanceof ClaimReportVo && "status".equals(name);
//    }
//
//    private static void autoSizeColumns(Sheet sheet, int columnNumber) {
//        for (int i = 0; i < columnNumber; i++) {
//            // sheet.autoSizeColumn(i, true);
//            sheet.setColumnWidth(i, 22 * 256);
//        }
//    }
//
//    private static CellStyle getCellStyle(SXSSFWorkbook wb) {
//        CellStyle style = wb.createCellStyle();
//        style.setBorderBottom(CellStyle.BORDER_THIN);
//        style.setBorderLeft(CellStyle.BORDER_THIN);
//        style.setBorderTop(CellStyle.BORDER_THIN);
//        style.setBorderRight(CellStyle.BORDER_THIN);
//        // style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        // style.setAlignment(CellStyle.ALIGN_LEFT);
//        // style.setWrapText(true);
//        return style;
//    }
//
//    private static int getHeight(String s) {
//        int h = 0;
//        if (null == s || "".equals(s)) {
//            h = 20;
//        } else {
//            String[] split = s.split("\n");
//            if (split.length * 15 >= 400) {
//                h = 400;
//            } else {
//                h = split.length * 20;
//            }
//        }
//        return h;
//    }
//
//    public static void export(HttpServletResponse response, SXSSFWorkbook workBook, String name) throws UnsupportedEncodingException {
//        String format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
//        String fileName = URLEncoder.encode(name + "_" + format + ".xlsx", "utf-8");
//        // response.setHeader("Accept-Ranges","bytes");
//        // response.setHeader("ETag", "");
//        response.setContentType("application/octet-stream");
//        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
//        OutputStream os = null;
//        try {
//            os = response.getOutputStream();
//            workBook.write(os);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (null != os) {
//                try {
//                    os.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}

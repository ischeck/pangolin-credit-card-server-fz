package cn.fintecher.pangolin.common.utils;

import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.UploadFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author:peishouwen
 * @Desc: 导出数据到Excel
 * @Date:Create in 18:38 2018/8/29
 */
public class ExportDataToExcelUtil {

    /**
     * Excel 文件类型
     */
    public static final String EXCEL_TYPE_XLSX = "xlsx";

     public  static  final int ROW_MEMORY = 1000;


    /**
     *
     * @param dataList 原始数据
     * @param titleNames 标题项
     * @param dataPros 属性值
     * @param filePath 文件路径+文件名称
     */
    public static void exportToExcel(List<?> dataList, String[] titleNames, String[] dataPros,String filePath) throws BadRequestException{
        if(Objects.isNull(dataList) || dataList.isEmpty()){
            throw  new BadRequestException(null, "exportToExcel", "无相应数据供导出");
        }
        FileOutputStream fos=null;
        SXSSFWorkbook wb=null;
        try {
             fos = new FileOutputStream(filePath);
             wb = new SXSSFWorkbook(ROW_MEMORY);
             CellStyle cellHeadStyle=headCellType(wb);
             CellStyle dataStyle=dataCellType(wb);
            //写入头信息
            Sheet sheet = null;
            sheet = wb.createSheet("sheet");
            Row row = sheet.createRow(0);
            for(int i=0;i<titleNames.length;i++){
                Cell cell = row.createCell(i);
                cell.setCellStyle(cellHeadStyle);
                cell.setCellValue(titleNames[i]);
                sheet.setColumnWidth(i, 7000);
            }
            for (int rowIndex=0;rowIndex<dataList.size();rowIndex++){
               Row dataRow = sheet.createRow(rowIndex+1);
                for (int colIndex=0;colIndex<dataPros.length;colIndex++){
                  Cell  cell = dataRow.createCell(colIndex);
                    cell.setCellStyle(dataStyle);
                    Field field = dataList.get(rowIndex).getClass().getDeclaredField(dataPros[colIndex]);
                    if(Objects.nonNull(field)) {
                        //设置对象的访问权限，保证对private的属性的访问
                        field.setAccessible(true);
                        if (Objects.equals(field.getType().getName(), "java.util.Date")) {
                            cell.setCellValue(Objects.nonNull(field.get(dataList.get(rowIndex))) ? ZWDateUtil.fomratterDate((Date) field.get(dataList.get(rowIndex)), "yyy-MM-dd HH:mm:ss") : null);
                        } else {
                            cell.setCellValue(Objects.nonNull(field.get(dataList.get(rowIndex))) ? field.get(dataList.get(rowIndex)).toString() : null);
                        }
                    }
                }
            }
            wb.write(fos);
            fos.flush();
        }catch (Exception e){
            e.printStackTrace();
            throw  new BadRequestException(null, "exportToExcel", "导出数据失败");
        }finally {
            try {
                if(Objects.isNull(fos)){
                    fos.flush();
                    fos.close();
                }
                if(Objects.isNull(wb)){
                    wb.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            //手动清除list
            dataList.clear();
        }
    }

    /**
     * 标题
     * @param wb
     * @return
     */
    private static CellStyle headCellType(SXSSFWorkbook wb){
        CellStyle cellHeadStyle = wb.createCellStyle();
        //边框
        cellHeadStyle.setBorderBottom(BorderStyle.DOUBLE);
        cellHeadStyle.setBorderLeft(BorderStyle.THICK);
        cellHeadStyle.setBorderRight(BorderStyle.DASH_DOT);
        cellHeadStyle.setBorderTop(BorderStyle.MEDIUM);
        cellHeadStyle.setAlignment(HorizontalAlignment.CENTER);
        //自动换行
        cellHeadStyle.setWrapText(false);
        //字体设置
        Font f = wb.createFont();
        f.setFontHeightInPoints((short) 12);
        f.setBold(true);
        cellHeadStyle.setFont(f);
        return cellHeadStyle;
    }

    /**
     * 数据
     * @param wb
     * @return
     */
    private static CellStyle dataCellType(SXSSFWorkbook wb){
        CellStyle dataCellType = wb.createCellStyle();
        //边框
        dataCellType.setBorderBottom(BorderStyle.DOUBLE);
        dataCellType.setBorderLeft(BorderStyle.THICK);
        dataCellType.setBorderRight(BorderStyle.DASH_DOT);
        dataCellType.setBorderTop(BorderStyle.MEDIUM);
        dataCellType.setAlignment(HorizontalAlignment.CENTER);
        //自动换行
        dataCellType.setWrapText(true);
        //字体设置
        Font f = wb.createFont();
        f.setFontHeightInPoints((short) 9);
        f.setBold(false);
        dataCellType.setFont(f);
        return dataCellType;
    }
}

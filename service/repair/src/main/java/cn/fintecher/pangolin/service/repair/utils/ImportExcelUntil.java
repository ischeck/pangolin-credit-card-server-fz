package cn.fintecher.pangolin.service.repair.utils;

import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Author:hanwannan
 * @Desc:
 * @Date:Create in 9:48 2018/9/1
 */
public class ImportExcelUntil {
	private static Logger logger= LoggerFactory.getLogger(ImportExcelUntil.class);
    /**
     * 拼装单个obj
     * @param clazz
     * @param sheet
     * @return
     * @throws Exception
     */
    private static <T> T dataObj(Class<T> clazz, String template, Sheet sheet, int rowIndex, List<String> errorList) throws Exception {
        //json数据模板转换为map
        Gson gson=new Gson();
        HashMap templateMap=gson.fromJson(template, HashMap.class);

        //获取标题row
        Row header = sheet.getRow(0);
        //获取当前row
        Row row = sheet.getRow(rowIndex);

        //容器
        T t=clazz.newInstance();
        for(Field field:clazz.getDeclaredFields()) {
            System.out.println(field.getName());
        }

        //注意excel表格字段顺序要和obj字段顺序对齐 （如果有多余字段请另作特殊下标对应处理）
        Field field=null;
        String headTitle=null;
        for(int colIndex = 0;colIndex < row.getPhysicalNumberOfCells(); colIndex++) {
        		headTitle=header.getCell(colIndex).getStringCellValue().trim();
        		if(templateMap.get(headTitle)!=null) {
        			String fieldName=(String)templateMap.get(headTitle);
        			field=t.getClass().getDeclaredField(fieldName);
            		//打开实体中私有变量的权限
            		field.setAccessible(true);
            		field.set(t, getVal(field, row.getCell(colIndex), rowIndex, colIndex, errorList));
        		}
        }

        //设置导入日期
        field=t.getClass().getDeclaredField("importDate");
        field.setAccessible(true);
        field.set(t, new Date());

        return t;
    }

    public  static <T> List<T> importExcel(Class<T> clazz, String template, InputStream inputStream, List<String> errorList) throws Exception {

        //装载流
        Workbook hw = WorkbookFactory.create(inputStream);
        //获取第一个sheet页
        Sheet sheet = hw.getSheetAt(0);
        //容器
        List<T> ret = new ArrayList<>();

        //遍历行 从下标第一行开始（去除标题）
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if(row!=null){
                //装载obj
            		if(row.getCell(0)!=null){
            			ret.add(dataObj(clazz, template, sheet, i, errorList));
            		}
            }
        }
        return ret;
    }

    /**
     * 处理val
     * @param cell
     * @return
     */
    private static Object getVal(Field field, Cell cell, int rowIndex, int colIndex, List<String> errorList) {
        String cellValue=null;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR:
                cellValue = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = String.valueOf(ZWDateUtil.fomratterDate(cell.getDateCellValue(), "yyyy/MM/dd"));
                } else {
                    DecimalFormat df = new DecimalFormat("#.#########");
                    cellValue = df.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_FORMULA:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = String.valueOf(ZWDateUtil.fomratterDate(cell.getDateCellValue(), "yyyy/MM/dd"));
                } else {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_STRING:
                cellValue = StringUtils.trim(cell.getStringCellValue());
                break;
            default:
                break;
        }

        if(StringUtils.isEmpty(cellValue)){
            return null;
        }
        Object obj=null;
        Class fieldDataType = field.getType();
        if(fieldDataType==String.class) {
        		obj=cellValue;
        }else if(fieldDataType==Integer.class){
        		try {
                obj=Integer.parseInt(cellValue);
            } catch (NumberFormatException e) {
                logger.error(e.getMessage(),e);
                errorList.add(createErrorStr(cellValue, "数值", rowIndex, colIndex , 1));
            }
        }else if(fieldDataType==Double.class){
        		try {
                BigDecimal bigDecimal=new BigDecimal(cellValue.replaceAll(",",""));
                obj = Math.abs(bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
            } catch (NumberFormatException e) {
                logger.error(e.getMessage(),e);
                errorList.add(createErrorStr(cellValue, "数值", rowIndex, colIndex,1));
            }
        }else if(fieldDataType==Date.class){
        		try {
                if (cellValue.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
                    obj= ZWDateUtil.getUtilDate(cellValue, "yyyy/MM/dd");
                } else if (cellValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    obj=ZWDateUtil.getUtilDate(cellValue, "yyyy-MM-dd");
                } else if (cellValue.matches("^\\d{4}\\d{2}\\d{2}")) {
                    obj=ZWDateUtil.getUtilDate(cellValue, "yyyyMMdd");
                } else if (cellValue.matches("\\d{4}.\\d{1,2}.\\d{1,2}")) {
                    obj=ZWDateUtil.getUtilDate(cellValue, "yyyy.MM.dd");
                } else if(cellValue.matches("\\d{1,2}/\\d{1,2}/\\d{4}")){
                    obj= ZWDateUtil.getUtilDate(cellValue, "MM/dd/yyyy");
                }else if(cellValue.matches("\\d{1,2}/\\d{1,2}/\\d{2}")){
                    obj= ZWDateUtil.getUtilDate(cellValue, "M/dd/yy");
                }else {
                    obj=null;
                    logger.error("日期格式无法匹配: {}",cellValue);
                }
            }catch (Exception e){
                	logger.error(e.getMessage(),e);
                errorList.add(createErrorStr(cellValue, "日期", rowIndex, colIndex,1));
            }
        }else{
        		obj=cellValue;
        }
        return obj;
    }
    
    /**
     * 拼装错误信息
     * @param cellValue
     * @param remark
     * @param rowIndex
     * @param colIndex
     * @return
     */
    private static String createErrorStr(String cellValue,String remark,int rowIndex,int colIndex,int sheetNum){
        return "第[".concat(String.valueOf(sheetNum)).concat("]sheet页的第[")
                .concat(String.valueOf(rowIndex)).concat("]行,第[")
                .concat(String.valueOf(colIndex)).concat("]列的值[")
                .concat(cellValue).concat("]转为[").concat(remark).concat("]");
    }

    public static void main(String[] args) {
		Gson gson=new Gson();
		Map map = Maps.newHashMap();
		map.put("colour", "red");
		map.put("weight", "10kg");
		String mapJson =  gson.toJson(map);  
		System.out.println(mapJson);
	}
}

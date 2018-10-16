package cn.fintecher.pangolin.common.utils;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Author:peishouwen
 * @Desc: SAX EventModel方式解析Excel工具类
 * @Date:Create in 16:18 2018/7/24
 */
public class SaxParseExcelUtil {

    /**
     * Excel 文件类型
     */
    public static final String EXCEL_TYPE_XLSX = "xlsx";

    /**
     * 解析Excel
     *
     * @param in          文件流
     * @param startRow    开始行
     * @param startCol    开始列
     * @param limit       读取数据行数 -1表示不做限制
     * @param sheetTotals sheet页总数
     * @return Map<Integer,List<Map<String,String>>> 返回各sheet页数据集合
     */
    public static Map<Integer, List<Map<String, String>>> parseExcel(InputStream in, int startRow, int startCol, int limit, int sheetTotals) throws Exception {
        Map<Integer, List<Map<String, String>>> dataMap = new HashMap<>();
        OPCPackage pkg = null;
        try {
            if (startRow > 0) {
                startRow = startRow - 1;
            }
            if (startCol > 0) {
                startCol = startCol - 1;
            }
            if (limit > 0) {
                limit = limit - 1;
            }
            pkg = OPCPackage.open(in);
            XSSFReader xssfReader = new XSSFReader(pkg);
            StylesTable styles = xssfReader.getStylesTable();
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            Iterator<InputStream> iterators = xssfReader.getSheetsData();
            int sheetCount = 1;
            while (iterators.hasNext()) {
                if (sheetCount > sheetTotals) {
                    break;
                }
                List<Map<String, String>> dataListMap = new ArrayList<>();
                SaxSheetContentsHandler sheetHandler = new SaxSheetContentsHandler(dataListMap, startRow, startCol, limit);
                processSheet(styles, strings, iterators.next(), sheetHandler);
                dataMap.put(Integer.valueOf(sheetCount), dataListMap);
                sheetCount = sheetCount + 1;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (pkg != null) {
                try {
                    pkg.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return dataMap;
    }

    /**
     * 解析每个sheet页数据
     *
     * @param styles
     * @param strings
     * @param sheetInputStream
     * @param saxSheetContentsHandler
     * @throws Exception
     */
    private static void processSheet(StylesTable styles, ReadOnlySharedStringsTable strings, InputStream sheetInputStream, SaxSheetContentsHandler saxSheetContentsHandler)
            throws Exception {
        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            sheetParser.setContentHandler(new XSSFSheetXMLHandler(styles, strings, saxSheetContentsHandler, false));
            sheetParser.parse(new InputSource(sheetInputStream));
        } catch (Exception e) {
            throw e;
        } finally {
            if (Objects.nonNull(sheetInputStream)) {
                sheetInputStream.close();
            }
        }
    }

    /**
     * 字母转为数字
     *
     * @param colStr
     * @param length
     * @return
     */
    public static Integer excelColStrToNum(String colStr, int length) {
        int num = 0;
        int result = 0;
        for (int i = 0; i < length; i++) {
            char ch = colStr.charAt(length - i - 1);
            num = (int) (ch - 'A' + 1);
            num *= Math.pow(26, i);
            result += num;
        }
        return result;
    }
}

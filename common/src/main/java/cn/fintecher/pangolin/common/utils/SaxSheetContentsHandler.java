package cn.fintecher.pangolin.common.utils;

import cn.fintecher.pangolin.common.model.CellDataModel;
import lombok.Data;
import org.apache.http.client.utils.CloneUtils;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author:peishouwen
 * @Desc: Excel解析处理器
 * @Date:Create in 14:05 2018/6/15
 */
@Data
public class SaxSheetContentsHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

    Logger logger= LoggerFactory.getLogger(SaxSheetContentsHandler.class);
    //数据单元
    private Map<String,String> cellMap=new HashMap<>();
    //数据记录，按行记录
    private List<Map<String,String>> cellDataListMap;
    //解析开始行
    private int startRow=0;
    //解析开始列
    private int startCol=0;
    //当前列
    private int currentCol=0;
    //读取数据行(默认为-1不做限制)
    private int limit=-1;

    /**
     * 构造器
     * @param cellDataListMap
     * @param startRow
     * @param startCol
     */
    public SaxSheetContentsHandler(List<Map<String,String>> cellDataListMap, int startRow, int startCol,int limit){
        this.cellDataListMap=cellDataListMap;
        this.startCol=startCol;
        this.startRow=startRow;
        this.limit=limit;
    }

    @Override
    public void startRow(int rowNum) {
        if(limit==-1){
            if(rowNum<startRow){
                return;
            }
        }else {
            if(rowNum<startRow || rowNum>limit){
                return;
            }
        }
        cellMap.clear();
    }

    @Override
    public void endRow(int rowNum) {
        if(limit==-1){
            if(rowNum<startRow){

                return;
            }
        }else if(rowNum<startRow || rowNum>limit) {
            currentCol=0;
            return;
        }
        if(!cellMap.isEmpty()){
            Map<String,String>  rowMap = null;
            try {
                rowMap = (Map<String, String>) CloneUtils.clone(cellMap);
                cellDataListMap.add(rowMap);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        currentCol=0;
    }

    /**
     *
     * @param cellReference 列名
     * @param formattedValue 单元格值
     * @param comment
     */
    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
        currentCol=currentCol+1;
        if(currentCol>=startCol){
            cellMap.put( cellReference.replaceAll("[^A-Za-z]",""),formattedValue);
        }
        return;

    }

    @Override
    public void headerFooter(String text, boolean isHeader, String tagName) {
        logger.debug("text: {},isHeader: {},tagName : {}",text,isHeader,tagName);
    }
}

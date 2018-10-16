package cn.fintecher.pangolin;

import cn.fintecher.pangolin.data.creation.util.CreateIDCardNo;
import cn.fintecher.pangolin.data.creation.util.RandomValue;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.DocxRenderData;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ChenChang on 2018/8/17.
 */

public class MultiWordExport {
    public static void main(String[] args) throws IOException {

        List<Map<String, Object>> dataArrayList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Map<String, String> map = RandomValue.getAddress();
            Map<String, Object> param = new HashMap<>();
            param.put("name", map.get("name"));
            param.put("idcardnumber", CreateIDCardNo.getRandomID());
            param.put("cardnumber", CreateIDCardNo.getRandomID());
            param.put("year", LocalDate.now().getYear());
            param.put("month", LocalDate.now().getMonthOfYear());
            param.put("day", LocalDate.now().getDayOfMonth());
            param.put("money", "4223.02");
            param.put("dmoney", "89.50");
            param.put("tel", "0991-2653610");
            param.put("y1", LocalDate.now().getYear());
            param.put("m1", LocalDate.now().getMonthOfYear());
            param.put("d1", LocalDate.now().getDayOfMonth());
            dataArrayList.add(param);
        }

        DocxRenderData segment = new DocxRenderData(new File("d:/zs.docx"), dataArrayList);
        Map<String, Object> word = new HashMap<>();

        word.put("docx_word", segment);
        XWPFTemplate template = XWPFTemplate.compile("d:/e.docx").render(word);


        FileOutputStream out = new FileOutputStream("d:/out.docx");

        template.write(out);
        out.flush();
        out.close();
        template.close();
    }
}

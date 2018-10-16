package cn.fintecher.pangolin;

import cn.fintecher.pangolin.data.creation.util.CreateIDCardNo;
import cn.fintecher.pangolin.data.creation.util.RandomValue;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import org.joda.time.LocalDate;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by ChenChang on 2018/8/17.
 */
public class MultiPdfExport {

    public static void main(String[] args) throws IOException, DocumentException {
        FileOutputStream out = new FileOutputStream("d:/d2.pdf");//输出流


        List<ByteArrayOutputStream> streams = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            PdfReader reader = new PdfReader("d:/d1.pdf");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, bos);
//     模板直接设定中文字体 这里不需要   Font fontZh = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 14, Font.BOLD, new CMYKColor(0, 255, 0, 0));
            AcroFields fields = stamper.getAcroFields();
            Iterator<String> it = fields.getFields().keySet().iterator();

            Map<String, String> map = RandomValue.getAddress();
            Map<String, String> param = new HashMap<>();
            param.put("name", map.get("name"));
            param.put("id_card", CreateIDCardNo.getRandomID());
            param.put("card_no", CreateIDCardNo.getRandomID());
            param.put("y1", String.valueOf(LocalDate.now().getYear()));
            param.put("m1", String.valueOf(LocalDate.now().getMonthOfYear()));
            param.put("d1", String.valueOf(LocalDate.now().getDayOfMonth()));
            param.put("money", "4223.02");
            param.put("dmoney", "89.50");
            param.put("tel", "0991-2653610");
            param.put("y2", String.valueOf(LocalDate.now().getYear()));
            param.put("m2", String.valueOf(LocalDate.now().getMonthOfYear()));
            param.put("d2", String.valueOf(LocalDate.now().getDayOfMonth()));

            while (it.hasNext()) {
                String name = it.next();
                fields.setField(name, param.get(name));

            }
            stamper.setFormFlattening(true);//如果为false那么生成的PDF文件还能编辑，一定要设为true
            stamper.close();
            streams.add(bos);
        }
        Document doc = new Document();
        PdfCopy copy = new PdfCopy(doc, out);
        doc.open();
        for (int i = 0; i < streams.size(); i++) {
            PdfImportedPage importPage = copy.getImportedPage(
                    new PdfReader(streams.get(i).toByteArray()), 1);
            copy.addPage(importPage);
        }
        doc.close();
    }
}

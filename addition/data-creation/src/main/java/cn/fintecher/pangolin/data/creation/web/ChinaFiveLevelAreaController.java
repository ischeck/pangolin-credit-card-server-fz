package cn.fintecher.pangolin.data.creation.web;


import cn.fintecher.pangolin.common.enums.AreaType;
import cn.fintecher.pangolin.data.creation.respository.ChinaFiveLevelAreaRepository;
import cn.fintecher.pangolin.entity.domain.ChinaFiveLevelArea;
import io.swagger.annotations.Api;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ChenChang on 2017/8/4.
 */
@RestController
@RequestMapping("/api/chinaFive")
@Api(value = "中国5级区域", description = "中国5级区域")
public class ChinaFiveLevelAreaController {
    private final Logger log = LoggerFactory.getLogger(ChinaFiveLevelAreaController.class);
    @Autowired
    private ChinaFiveLevelAreaRepository chinaFiveLevelAreaRepository;

    public static void main(String[] args) throws IOException {
        Reader in = new FileReader("d:/area_code.csv");
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
        for (CSVRecord record : records) {
            System.err.println(record.size());
            System.err.println(record.get(0) + "|" + record.get(1) + "|" + record.get(2) + "|");
            break;
        }
    }

    @PostMapping(value = "/insert")
    public ResponseEntity<Void> insert(@RequestParam String csvPath) throws IOException {
        Reader in = new FileReader(csvPath);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

        Map<String, ChinaFiveLevelArea> areaMap = new HashMap<>();
        for (CSVRecord record : records) {
            ChinaFiveLevelArea area = new ChinaFiveLevelArea();
            switch (record.get(0)) {
                case "1":
                    //省
                    area.setId(record.get(1));
                    area.setName(record.get(2));
                    area.setCode(record.get(1));
                    area.setType(AreaType.PROVINCE);
                    break;
                case "2":
                    //市
                    area.setId(record.get(3));
                    area.setName(record.get(4));
                    area.setCode(record.get(3));
                    area.setType(AreaType.CITY);
                    area.setParent(areaMap.get(record.get(1)));
                    break;
                case "3":
                    //区县
                    area.setId(record.get(5));
                    area.setName(record.get(6));
                    area.setCode(record.get(5));
                    area.setType(AreaType.COUNTY);
                    area.setParent(areaMap.get(record.get(3)));
                    break;
                case "4":
                    //乡镇
                    area.setId(record.get(7));
                    area.setName(record.get(8));
                    area.setCode(record.get(7));
                    area.setType(AreaType.TOWN);
                    area.setParent(areaMap.get(record.get(5)));
                    break;
                case "5":
                    //村 居委会
                    area.setId(record.get(9));
                    area.setName(record.get(10));
                    area.setCode(record.get(9));
                    area.setType(AreaType.VILLAGE);
                    area.setParent(areaMap.get(record.get(7)));
                    break;
            }
            areaMap.put(area.getId(), area);

        }
        chinaFiveLevelAreaRepository.saveAll(areaMap.values());
        return ResponseEntity.ok().build();
    }
}

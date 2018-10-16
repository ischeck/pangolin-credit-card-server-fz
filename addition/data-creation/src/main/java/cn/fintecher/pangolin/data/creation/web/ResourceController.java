package cn.fintecher.pangolin.data.creation.web;


import cn.fintecher.pangolin.common.enums.ResourceType;
import cn.fintecher.pangolin.data.creation.respository.ResourceRepository;
import cn.fintecher.pangolin.entity.managentment.Resource;
import io.swagger.annotations.Api;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("/api/resource")
@Api(value = "资源", description = "资源")
public class ResourceController {
    private final Logger log = LoggerFactory.getLogger(ResourceController.class);
    @Autowired
    private ResourceRepository resourceRepository;

    public static void main(String[] args) throws IOException {
        Reader in = new FileReader("d:/res.csv");
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
        for (CSVRecord record : records) {
            System.out.println(record.getRecordNumber());
            break;
        }
    }

    @PostMapping(value = "/insert")
    public ResponseEntity<Void> insert(@RequestParam String csvPath) throws IOException {
        Reader in = new FileReader(csvPath);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

        Map<String, Resource> resourceMap = new HashMap<>();
        for (CSVRecord record : records) {
            if (record.getRecordNumber() == 1) {
                continue;
            }
            Resource resource = new Resource();
            resource.setId(record.get(0));
            switch (record.get(5)) {
                case "17":
                    resource.setType(ResourceType.DIRECTORY);
                    resource.setName(record.get(1));
                    resource.setLevel(1);
                    break;
                case "18":
                    resource.setType(ResourceType.DIRECTORY);
                    resource.setName(record.get(2));
                    resource.setLevel(2);
                    break;
                case "19":
                    resource.setType(ResourceType.MENU);
                    resource.setName(record.get(3));
                    resource.setLevel(3);
                    break;
            }
            if (StringUtils.isNotBlank(record.get(4))) {
                resource.setParent(record.get(4));

            }
            resource.setSort(Integer.valueOf(record.get(6)));
            resourceMap.put(resource.getId(), resource);
        }
        resourceRepository.deleteAll();
        resourceRepository.insert(resourceMap.values());
        return ResponseEntity.ok().build();
    }
}

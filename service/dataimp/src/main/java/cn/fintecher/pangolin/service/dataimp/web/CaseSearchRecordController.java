package cn.fintecher.pangolin.service.dataimp.web;

import cn.fintecher.pangolin.entity.domain.LeftAmtLog;
import cn.fintecher.pangolin.entity.domain.PayAmtLog;
import cn.fintecher.pangolin.service.dataimp.repository.ImpLeftAmtLogRepPository;
import cn.fintecher.pangolin.service.dataimp.repository.ImpPayAmtLogRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IteratorUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:胡艳敏
 * @Desc: 查询对账/更新记录
 * @Date:Create 2018/8/6
 */
@RestController
@RequestMapping("/api/caseSearchRecordController")
@Api(value = "查询对账/更新记录", description = "查询对账/更新记录")
public class CaseSearchRecordController {
    Logger logger = LoggerFactory.getLogger(CaseSearchRecordController.class);

    @Autowired
    ImpLeftAmtLogRepPository impLeftAmtLogRepPository;

    @Autowired
    ImpPayAmtLogRepository impPayAmtLogRepository;

    @Autowired
    ModelMapper modelMapper;


    @ApiOperation(value = "查询对账记录", notes = "查询对账记录")
    @GetMapping("/caseBillRecord")
    public ResponseEntity caseBillRecord(String baseCaseId) {
        logger.info("search CaseBill record {}", baseCaseId);
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(matchPhraseQuery("caseId", baseCaseId));
        Iterable<LeftAmtLog> search = impLeftAmtLogRepPository.search(builder);
        List<LeftAmtLog> list = new ArrayList<>();
        if (search.iterator().hasNext()) {
            list = IteratorUtils.toList(search.iterator());
        }
        return ResponseEntity.ok().body(list);

    }

    @ApiOperation(value = "查询更新记录", notes = "查询更新记录")
    @GetMapping("/caseUpdateRecord")
    public ResponseEntity caseUpdateRecord(String baseCaseId) {
        logger.info("search CaseBill record {}", baseCaseId);
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(matchPhraseQuery("caseId", baseCaseId));
        Iterable<PayAmtLog> search = impPayAmtLogRepository.search(builder);
        List<PayAmtLog> list = new ArrayList<>();
        if (search.iterator().hasNext()) {
            list = IteratorUtils.toList(search.iterator());
        }
        return ResponseEntity.ok().body(list);
    }

}

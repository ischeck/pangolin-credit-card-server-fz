package cn.fintecher.pangolin.service.domain.web;

import cn.fintecher.pangolin.common.enums.ApplyType;
import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.enums.PaymentStatus;
import cn.fintecher.pangolin.common.enums.PaymentType;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.domain.PaymentRecord;
import cn.fintecher.pangolin.service.domain.model.PaymentRecordModel;
import cn.fintecher.pangolin.service.domain.model.request.ApplyCaseRequest;
import cn.fintecher.pangolin.service.domain.respository.PaymentRecordRepository;
import cn.fintecher.pangolin.service.domain.service.CaseApplyService;
import cn.fintecher.pangolin.service.domain.service.DomainBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IteratorUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * @Author:胡艳敏
 * @Desc: 还款记录
 * @Date:Create 2018/8/7
 */
@RestController
@RequestMapping("/api/paymentRecordController")
@Api(value = "查询还款记录", description = "查询还款记录")
public class PaymentRecordController {
    Logger logger = LoggerFactory.getLogger(PaymentRecordController.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Autowired
    private DomainBaseService domainBaseService;

    @Autowired
    private CaseApplyService caseApplyService;


    @ApiOperation(value = "查询PTP记录", notes = "查询PTP记录")
    @GetMapping("/searchPTPRecord")
    public ResponseEntity searchPTPRecord(String baseCaseId) {
        logger.info("search PTP record {}", baseCaseId);
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(matchPhraseQuery("caseId", baseCaseId))
                .must(QueryBuilders.boolQuery().should(matchPhraseQuery("paymentStatus", PaymentStatus.WAIT_CONFIRMED.toString()))
                        .should(matchPhraseQuery("isBouncedCheck", ManagementType.YES.toString())));
        Iterable<PaymentRecord> search = paymentRecordRepository.search(qb);
        List<PaymentRecordModel> list = new ArrayList<>();
        if (search.iterator().hasNext()) {
            List<PaymentRecord> paymentRecords = IteratorUtils.toList(search.iterator());
            Type listMap = new TypeToken<List<PaymentRecordModel>>() {
            }.getType();
            list = modelMapper.map(paymentRecords, listMap);
        }
        return ResponseEntity.ok().body(list);
    }

    @ApiOperation(value = "查询CP记录", notes = "查询CP记录")
    @GetMapping("/searchCPRecord")
    public ResponseEntity searchCPRecord(String baseCaseId) {
        logger.info("search PTP record {}", baseCaseId);
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
            qb.must(matchPhraseQuery("caseId", baseCaseId))
                    .must(QueryBuilders.boolQuery().should(matchPhraseQuery("paymentStatus", PaymentStatus.CONFIRMED.toString()))
                            .should(matchPhraseQuery("paymentStatus", PaymentStatus.CONFIRMING.toString())))
                    .mustNot(matchPhraseQuery("isBouncedCheck", ManagementType.YES.toString()));
        Iterable<PaymentRecord> search = paymentRecordRepository.search(qb);
        List<PaymentRecordModel> list = new ArrayList<>();
        if (search.iterator().hasNext()) {
            List<PaymentRecord> paymentRecords = IteratorUtils.toList(search.iterator());
            Type listMap = new TypeToken<List<PaymentRecordModel>>() {
            }.getType();
            list = modelMapper.map(paymentRecords, listMap);
        }
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/transferPTPToCP")
    @ApiOperation(value = "PTP转CP", notes = "PTP转CP")
    public ResponseEntity transferPTPToCP(@RequestBody ApplyCaseRequest request,
                                          @RequestHeader(value = "X-UserToken") String token) {
        logger.info("search PTP record {}", request);
        OperatorModel operatorModel = domainBaseService.getOperator(token);
        request.setApplyType(ApplyType.CHECK_OVERDUE_AMOUNT_APPLY);
        Optional<PaymentRecord> byId = paymentRecordRepository.findById(request.getPaymentRecordId());
        byId.orElseThrow(()->new BadRequestException(null,"paymentRecord","paymentRecord.is.not.exist"));
        PaymentRecord paymentRecord = byId.get();
        caseApplyService.setObjectCaseApply(request, operatorModel);
        paymentRecord.setPaymentStatus(PaymentStatus.CONFIRMING);
        paymentRecord.setOperatorDate(ZWDateUtil.getNowDate());
        paymentRecordRepository.save(paymentRecord);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/paymentPTPBouncedCheck")
    @ApiOperation(value = "PTP跳票", notes = "PTP跳票")
    public ResponseEntity paymentPTPBouncedCheck(String paymentId,
                                          @RequestHeader(value = "X-UserToken") String token) {
        logger.info("search PTP record {}", paymentId);
        Optional<PaymentRecord> byId = paymentRecordRepository.findById(paymentId);
        byId.orElseThrow(()->new BadRequestException(null,"paymentRecord","paymentRecord.is.not.exist"));
        PaymentRecord paymentRecord = byId.get();
        paymentRecord.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRecord.setIsBouncedCheck(ManagementType.YES);
        paymentRecord.setOperatorDate(ZWDateUtil.getNowDate());
        paymentRecordRepository.save(paymentRecord);
        return ResponseEntity.ok().body(null);
    }

}

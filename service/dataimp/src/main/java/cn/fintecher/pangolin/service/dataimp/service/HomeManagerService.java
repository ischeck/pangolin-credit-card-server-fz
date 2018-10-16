package cn.fintecher.pangolin.service.dataimp.service;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.ManagementType;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.service.dataimp.client.OrganizationClient;
import cn.fintecher.pangolin.service.dataimp.model.response.CountInfoResponse;
import cn.fintecher.pangolin.service.dataimp.repository.BalancePayRecordRepository;
import cn.fintecher.pangolin.service.dataimp.repository.ImportBaseCaseRepository;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

@Service("homeManagerService")
public class HomeManagerService {

    Logger log = LoggerFactory.getLogger(HomeCollectorService.class);

    @Autowired
    ImportBaseCaseRepository importBaseCaseRepository;
    @Autowired
    BalancePayRecordRepository balancePayRecordRepository;
    @Autowired
    OrganizationClient organizationClient;

    public CountInfoResponse getCountInfo(OperatorModel operator) {
        CountInfoResponse response = new CountInfoResponse();
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if (Objects.equals(operator.getIsManager(), ManagementType.YES)) {
            qb.must(termsQuery("departments.keyword", operator.getOrganization()));
        } else {
            qb.must(matchPhraseQuery("currentCollector.id.keyword", operator.getId()));
        }
        qb.must(matchPhraseQuery("caseDataStatus", CaseDataStatus.IN_POOL.name()));
        List<String> caseIds = new ArrayList<>();
        Integer collectorNum = organizationClient.getUserNumByOrgId(operator.getOrganization()).getBody();
        importBaseCaseRepository.search(qb).forEach(baseCase -> {
            caseIds.add(baseCase.getId());
            response.setTotalOverdueAmt(response.getTotalOverdueAmt()+baseCase.getOverdueAmtTotal());
        });
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(termsQuery("caseId", caseIds));
        balancePayRecordRepository.search(builder).forEach(balancePayRecord -> {
            response.setTotalReturnAmt(response.getTotalReturnAmt() + balancePayRecord.getPayAmt());
        });
        response.setTotalCaseNum(caseIds.size());
        response.setCollectorNum(collectorNum);
        response.setTotalReturnAmt(response.getTotalReturnAmt()/10000);
        response.setTotalOverdueAmt(response.getTotalOverdueAmt()/10000);
        response.setPerCaseNum(response.getTotalCaseNum()/response.getCollectorNum());
        response.setPerOverdueAmt(response.getTotalOverdueAmt()/response.getCollectorNum());
        response.setPerReturnAmt(response.getTotalReturnAmt()/response.getCollectorNum());
        return response;
    }


}

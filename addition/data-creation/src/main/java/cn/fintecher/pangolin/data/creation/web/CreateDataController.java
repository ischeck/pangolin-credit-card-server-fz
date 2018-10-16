package cn.fintecher.pangolin.data.creation.web;

import cn.fintecher.pangolin.common.enums.CaseDataStatus;
import cn.fintecher.pangolin.common.enums.CaseIssuedFlag;
import cn.fintecher.pangolin.common.enums.CaseLeaveFlag;
import cn.fintecher.pangolin.common.enums.ImportDataExcelStatus;
import cn.fintecher.pangolin.common.utils.Snowflake;
import cn.fintecher.pangolin.data.creation.respository.*;
import cn.fintecher.pangolin.data.creation.util.CreateIDCardNo;
import cn.fintecher.pangolin.data.creation.util.DateRandom;
import cn.fintecher.pangolin.data.creation.util.RandomValue;
import cn.fintecher.pangolin.entity.domain.*;
import cn.fintecher.pangolin.entity.managentment.Principal;
import io.swagger.annotations.Api;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by ChenChang on 2018/8/10.
 */
@RestController
@RequestMapping("/api/createDataController")
@Api(value = "数据创建", description = "数据创建")
public class CreateDataController {
    private final Logger log = LoggerFactory.getLogger(PersonController.class);
    List<Principal> principals;
    @Autowired
    private PersonalRepository personalRepository;
    @Autowired
    private BaseCaseRepository baseCaseRepository;
    @Autowired
    private PrincipalRepository principalRepository;
    @Autowired
    private PersonalContactImpRepository personalContactImpRepository;
    @Autowired
    private ImportDataExcelRecordRepository importDataExcelRecordRepository;

    @PostMapping("/add500WCase")
    public ResponseEntity<Void> add500WCase(@RequestParam String userName,@RequestParam String opName) {
        for (int i = 0; i < 25; i++) {
            String batchNum = String.valueOf(new Snowflake(i%1024).next());
            addRandomCase(20,userName,opName,batchNum);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addRandomCase")
    public ResponseEntity<Void> addRandomCase(@RequestParam Integer count,@RequestParam String userName,@RequestParam String opName,@RequestParam String batchNum) {
        log.debug("开始做假数据了");
        principals = principalRepository.findAll();
        Principal principal = randomPrincipal();
//        String batchNum = DateTime.now().toString("yyyyMMdd") + RandomUtils.nextInt(1000, 9999);
        ImportDataExcelRecord importDataExcelRecord = new ImportDataExcelRecord();
        importDataExcelRecord.setId(UUID.randomUUID().toString());
        importDataExcelRecord.setCaseTotal(new Long(count * 10000));
        importDataExcelRecord.setDelegationDate(new Date());
        importDataExcelRecord.setEndCaseDate(DateTime.now().plusMonths(3).toDate());
        importDataExcelRecord.setBatchNumber(batchNum);
        importDataExcelRecord.setPrincipalId(principal.getId());
        importDataExcelRecord.setPrincipalName(principal.getPrincipalName());
        importDataExcelRecord.setOperatorTime(new Date());
        importDataExcelRecord.setOperatorName(opName);
        importDataExcelRecord.setOperatorUserName(userName);
        importDataExcelRecord.setFileId("base_file");
//        importDataExcelRecord.setAreaNoDisTotal(importDataExcelRecord.getCaseTotal());
        importDataExcelRecord.setImportDataExcelStatus(ImportDataExcelStatus.CONFIRMED);
        importDataExcelRecord = importDataExcelRecordRepository.save(importDataExcelRecord);
        for (int i = 0; i < count; i++) {
            StopWatch stopWatch = new StopWatch("执行第" + i + "个");
            stopWatch.start("数据准备");
            List<Personal> personList = new ArrayList<>();
            List<BaseCase> caseList = new ArrayList<>();
            Set<PersonalContact> contacts = new HashSet<>();

            for (int j = 0; j < 10000; j++) {
                BaseCase baseCase = createCase(principal, batchNum, importDataExcelRecord);
                Personal personal = createPersonal(baseCase, contacts);
                personList.add(personal);
                caseList.add(baseCase);
            }
            stopWatch.stop();
            stopWatch.start("数据插入");
            personalRepository.saveAll(personList);
            baseCaseRepository.saveAll(caseList);
            personalContactImpRepository.saveAll(contacts);
            stopWatch.stop();
            log.info(stopWatch.prettyPrint());
        }
        return ResponseEntity.ok().build();
    }

    private Personal createPersonal(BaseCase baseCase, Set<PersonalContact> contacts) {
        Personal personal = baseCase.getPersonal();

        int number = new Random().nextInt(10) + 1;

        for (int i = 0; i < number; i++) {
            contacts.add(createPersonalContact(personal));
        }


        return personal;
    }

    private PersonalContact createPersonalContact(Personal personal) {
        PersonalContact personalContact = new PersonalContact();
        Map<String, String> map = RandomValue.getAddress();

        personalContact.setRelation(randomMarital());
        personalContact.setName(map.get("name"));
        //personalContact.setPhoneNo(map.get("tel"));
        personalContact.setCertificateNo(CreateIDCardNo.getRandomID());
        personalContact.setPersonalId(personal.getId());
       // personalContact.setPhoneState("正常");
        return personalContact;
    }

    private BaseCase createCase(Principal principal, String batchNumber, ImportDataExcelRecord importDataExcelRecord) {
        StopWatch watch = new StopWatch("创建案件");
        watch.start("RandomValue");
        Map<String, String> map = RandomValue.getAddress();
        watch.stop();
        watch.start("CreateIDCardNo");
        String idCard = CreateIDCardNo.getRandomID();
        watch.stop();
        watch.start("MD5Encoder");
        String pId = Md5Crypt.md5Crypt(idCard.getBytes());
        watch.stop();
        watch.start("其他");
        String name = map.get("name");
        watch.stop();
        watch.start("设置属性");
        BaseCase baseCase = new BaseCase();
        baseCase.setId(UUID.randomUUID().toString());
        baseCase.setPersonal(new Personal());


        baseCase.getPersonal().setPersonalName(name);
        baseCase.getPersonal().setId(pId);
//        baseCase.getPersonal().setPersonalNo(idCard);
        baseCase.getPersonal().setCertificateType("身份证");
        baseCase.getPersonal().setSex(randomSex());
        baseCase.getPersonal().setBirthday(LocalDate.now().minusYears(RandomUtils.nextInt(18, 50)).toDate());
        baseCase.getPersonal().setCertificateNo(idCard);
        //地址
        baseCase.getPersonal().setBillAddr(map.get("road"));
        baseCase.getPersonal().setEmployerAddr(map.get("road"));
        baseCase.getPersonal().setHomeAddr(map.get("road"));
//        baseCase.getPersonal().setPropertyAddr(map.get("road"));
        baseCase.getPersonal().setResidenceAddr(map.get("road"));
        //电话
        baseCase.getPersonal().setSelfPhoneNo(map.get("tel"));
        baseCase.getPersonal().setEmployerPhoneNo(map.get("tel"));
        baseCase.getPersonal().setHomePhoneNo(map.get("tel"));

        baseCase.setCaseDataStatus(CaseDataStatus.IN_POOL);
        baseCase.setBatchNumber(batchNumber);
        baseCase.setOperatorTime(new Date());
        baseCase.setOperator("");
        baseCase.setCollectionRecordCount(0);

        baseCase.setLeftAmt(randomDouble());
        //baseCase.setBillDay(RandomUtils.nextInt(0, 30));
        baseCase.setCapitalAmt(randomDouble());
        //baseCase.setMinPayAmt(randomDouble());
        baseCase.setInterestAmt(randomDouble());
        baseCase.setLeftAmtDollar(randomDouble());
        baseCase.setOverdueAmtTotal(randomDouble());
        baseCase.setOverdueAmtTotalDollar(randomDouble());
        //baseCase.setLatestPayAmt(randomDouble());
//        baseCase.setOutInterestAmt(randomDouble());
        //baseCase.setMinPayAmtDollar(randomDouble());
        baseCase.setLeaveFlag(CaseLeaveFlag.NO_LEAVE);
        baseCase.setCollectionTotalRecordCount(0);
        baseCase.setDelegationDate(importDataExcelRecord.getDelegationDate());
        baseCase.setEndCaseDate(importDataExcelRecord.getEndCaseDate());
//        baseCase.setDeleteCaseDate(DateTime.now().plusYears(2).toDate());
        baseCase.setHandsNumber(randomHand());

//        baseCase.setClearAccountDollar(String.valueOf(RandomUtils.nextLong(100000000L, 999999999L)));
//        baseCase.setClearAccountRMB(String.valueOf(RandomUtils.nextLong(100000000L, 999999999L)));
       // baseCase.setPayStatus("未还款");
//        baseCase.setOverdueStatus("M" + RandomUtils.nextInt(1, 3) + "");
        baseCase.setCity(randomCity());
        baseCase.setPrincipal(principal);
//        baseCase.setCaseStatus(new HashSet<>());
//        baseCase.getCaseStatus().add("");
        baseCase.setIssuedFlag(CaseIssuedFlag.AREA_UN_DIS);

        //baseCase.setCardInformationSet(new HashSet<>());
        CardInformation cardInformation = new CardInformation();
//        cardInformation.setCardNo(baseCase.getClearAccountRMB());
        cardInformation.setLimitAmt(RandomUtils.nextDouble(1000, 5000));
        cardInformation.setOpenAccountDate(LocalDate.now().minusYears(RandomUtils.nextInt(5, 10)).toDate());
        //baseCase.getCardInformationSet().add(cardInformation);

        watch.stop();
        //处理卡信息
        watch.start("CardInformation");
        Set<CardInformation> cardSet = new HashSet<>();
        int number = new Random().nextInt(3) + 1;
        for (int i = 0; i < number; i++) {
            cardSet.add(createCardInformation(name, idCard));
        }
        watch.stop();
        return baseCase;
    }

    private CardInformation createCardInformation(String name, String idcard) {
        CardInformation cardInformation = new CardInformation();
        Snowflake snowflake = new Snowflake((int) (System.currentTimeMillis() % 1024));
        String cardNo = String.valueOf(snowflake.next());
        cardInformation.setCardNo(cardNo);
//        cardInformation.setAccountType("信用卡");
        //cardInformation.setAccountNo(cardNo);
//        cardInformation.setActiveDate(DateRandom.randomDate());
        cardInformation.setOpenAccountDate(DateRandom.randomDate());
        return cardInformation;
    }

    private Double randomDouble() {
        DecimalFormat df = new DecimalFormat("#.00");
        return Double.valueOf(df.format(RandomUtils.nextDouble(100D, 10000D)));
    }


    private String randomSex() {
        List<String> list = new ArrayList<>();
        list.add("男");
        list.add("女");
        list.add("未知");
        int pick = new Random().nextInt(list.size());
        return list.get(pick);
    }

    private String randomMarital() {
        List<String> list = new ArrayList<>();
        list.add("本人");
        list.add("配偶");
        list.add("父母");
        list.add("家人");
        list.add("亲属");
        list.add("同事");
        int pick = new Random().nextInt(list.size());
        return list.get(pick);
    }

    private Principal randomPrincipal() {
        int pick = new Random().nextInt(principals.size());
        return principals.get(pick);
    }

    private String randomCity() {
        List<String> cityList = new ArrayList<>();
        cityList.add("西安");
        cityList.add("福州");
        cityList.add("南京");
        cityList.add("杭州");
        cityList.add("上海");
        cityList.add("北京");
        int pick = new Random().nextInt(cityList.size());
        return cityList.get(pick);
    }

    private String randomHand() {
        List<String> list = new ArrayList<>();
        list.add("一手");
        list.add("二手");
        list.add("三手");
        list.add("四手");
        int pick = new Random().nextInt(list.size());
        return list.get(pick);
    }


}

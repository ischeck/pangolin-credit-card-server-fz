package cn.fintecher.pangolin.service.management.service;


import cn.fintecher.pangolin.common.enums.ClockStatus;
import cn.fintecher.pangolin.common.enums.ClockType;
import cn.fintecher.pangolin.common.enums.OperatorState;
import cn.fintecher.pangolin.common.exception.BadRequestException;
import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.common.utils.ZWStringUtils;
import cn.fintecher.pangolin.entity.managentment.*;
import cn.fintecher.pangolin.service.management.model.request.CreateClockRecordRequest;
import cn.fintecher.pangolin.service.management.model.response.ClockRecordResponse;
import cn.fintecher.pangolin.service.management.repository.ClockConfigRepository;
import cn.fintecher.pangolin.service.management.repository.ClockRecordRepository;
import cn.fintecher.pangolin.service.management.repository.OperatorRepository;
import cn.fintecher.pangolin.service.management.repository.OrganizationRepository;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("clockService")
public class ClockService {

    Logger log = LoggerFactory.getLogger(ClockService.class);

    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    ClockRecordRepository clockRecordRepository;
    @Autowired
    ClockConfigRepository clockConfigRepository;
    @Autowired
    OperatorRepository operatorRepository;
    @Autowired
    OrganizationService organizationService;


    /**
     * 每天生成所有人员的初始打卡记录
     */
    public void createClockRecord() {
        try {
            log.info("开始生成打卡记录数据"+ZWDateUtil.getDate());
            List<ClockConfig> clockConfigs = clockConfigRepository.findAll();
            List<ClockRecord> clockRecords = new ArrayList<>();
            String date = ZWDateUtil.getDate();
            for (ClockConfig config : clockConfigs) {
                Date morningSignTime = null;
                Date morningAllowTimeMin = null;
                Date morningAllowTimeMax = null;
                Date noonSignTime = null;
                Date noonAllowTimeMin = null;
                Date noonAllowTimeMax = null;
                Date afterSignTime = null;
                Date afterAllowTimeMin = null;
                Date afterAllowTimeMax = null;
                for (ClockConfigDetail detail : config.getClockConfigDetails()) {
                    if (Objects.equals(detail.getClockType(), ClockType.MORNING)) {
                        morningSignTime = ZWDateUtil.getUtilDate(date + " " + detail.getSignTime(), "yyyy-MM-dd HH:mm:ss");
                        morningAllowTimeMin = ZWDateUtil.getUtilDate(date + " " + detail.getAllowTimeMin(), "yyyy-MM-dd HH:mm:ss");
                        morningAllowTimeMax = ZWDateUtil.getUtilDate(date + " " + detail.getAllowTimeMax(), "yyyy-MM-dd HH:mm:ss");
                    } else if (Objects.equals(detail.getClockType(), ClockType.NOON)) {
                        noonSignTime = ZWDateUtil.getUtilDate(date + " " + detail.getSignTime(), "yyyy-MM-dd HH:mm:ss");
                        noonAllowTimeMin = ZWDateUtil.getUtilDate(date + " " + detail.getAllowTimeMin(), "yyyy-MM-dd HH:mm:ss");
                        noonAllowTimeMax = ZWDateUtil.getUtilDate(date + " " + detail.getAllowTimeMax(), "yyyy-MM-dd HH:mm:ss");
                    } else {
                        afterSignTime = ZWDateUtil.getUtilDate(date + " " + detail.getSignTime(), "yyyy-MM-dd HH:mm:ss");
                        afterAllowTimeMin = ZWDateUtil.getUtilDate(date + " " + detail.getAllowTimeMin(), "yyyy-MM-dd HH:mm:ss");
                        afterAllowTimeMax = ZWDateUtil.getUtilDate(date + " " + detail.getAllowTimeMax(), "yyyy-MM-dd HH:mm:ss");
                    }
                }
                Organization organization = organizationRepository.findById(config.getOrganization()).get();
                List<Operator> operators = organizationService.getAllOperator(config.getOrganization());
                for (Operator operator : operators) {
                    ClockRecord record = new ClockRecord();
                    record.setMorningSignTime(morningSignTime);
                    record.setMorningAllowTimeMin(morningAllowTimeMin);
                    record.setMorningAllowTimeMax(morningAllowTimeMax);
                    record.setNoonSignTime(noonSignTime);
                    record.setNoonAllowTimeMin(noonAllowTimeMin);
                    record.setNoonAllowTimeMax(noonAllowTimeMax);
                    if (Objects.isNull(noonSignTime)) {
                        record.setNoonStatus(ClockStatus.NORMAL);
                    }
                    record.setAfterSignTime(afterSignTime);
                    record.setAfterAllowTimeMin(afterAllowTimeMin);
                    record.setAfterAllowTimeMax(afterAllowTimeMax);
                    record.setDate(date);
                    record.setOperator(operator.getId());
                    record.setOperatorName(operator.getFullName());
                    record.setOrganizationName(organization.getName());
                    record.setOrganizations(organizationService.getParentOrg(organization.getId()));
                    clockRecords.add(record);
                }
            }
            clockRecordRepository.saveAll(clockRecords);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 生成单条打卡记录
     *
     * @param organizationId
     * @param date
     * @param operatorId
     * @param fullName
     * @return
     */
    public ClockRecord createRecord(String organizationId, String date, String operatorId, String fullName) {
        ClockRecord clockRecord = new ClockRecord();
        Organization organization = organizationRepository.findById(organizationId).get();
        if (ZWStringUtils.isEmpty(organization.getClockConfigId())) {
            throw new BadRequestException(null, "", "clockConfig.not.exist");
        }
        Optional<ClockConfig> clockConfigOptional = clockConfigRepository.findById(organization.getClockConfigId());
        clockConfigOptional.orElseThrow(() -> new BadRequestException(null, "", "clockConfig.not.exist"));
        ClockConfig clockConfig = clockConfigOptional.get();
        clockConfig.getClockConfigDetails().forEach(e -> {
            try {
                if (Objects.equals(e.getClockType(), ClockType.MORNING)) {
                    clockRecord.setMorningSignTime(ZWDateUtil.getUtilDate(date + " " + e.getSignTime(), "yyyy-MM-dd HH:mm:ss"));
                    clockRecord.setMorningAllowTimeMin(ZWDateUtil.getUtilDate(date + " " + e.getAllowTimeMin(), "yyyy-MM-dd HH:mm:ss"));
                    clockRecord.setMorningAllowTimeMax(ZWDateUtil.getUtilDate(date + " " + e.getAllowTimeMax(), "yyyy-MM-dd HH:mm:ss"));
                } else if (Objects.equals(e.getClockType(), ClockType.NOON)) {
                    clockRecord.setNoonSignTime(ZWDateUtil.getUtilDate(date + " " + e.getSignTime(), "yyyy-MM-dd HH:mm:ss"));
                    clockRecord.setNoonAllowTimeMin(ZWDateUtil.getUtilDate(date + " " + e.getAllowTimeMin(), "yyyy-MM-dd HH:mm:ss"));
                    clockRecord.setNoonAllowTimeMax(ZWDateUtil.getUtilDate(date + " " + e.getAllowTimeMax(), "yyyy-MM-dd HH:mm:ss"));
                } else {
                    clockRecord.setAfterSignTime(ZWDateUtil.getUtilDate(date + " " + e.getSignTime(), "yyyy-MM-dd HH:mm:ss"));
                    clockRecord.setAfterAllowTimeMin(ZWDateUtil.getUtilDate(date + " " + e.getAllowTimeMin(), "yyyy-MM-dd HH:mm:ss"));
                    clockRecord.setAfterAllowTimeMax(ZWDateUtil.getUtilDate(date + " " + e.getAllowTimeMax(), "yyyy-MM-dd HH:mm:ss"));
                }
            } catch (Exception er) {
                log.error(er.getMessage(), er);
                return;
            }
        });
        if (Objects.isNull(clockRecord.getNoonSignTime())) {
            clockRecord.setNoonStatus(ClockStatus.NORMAL);
        }
        clockRecord.setDate(date);
        clockRecord.setOperator(operatorId);
        clockRecord.setOperatorName(fullName);
        clockRecord.setOrganizationName(organization.getName());
        clockRecord.setOrganizations(organizationService.getParentOrg(organizationId));
        return clockRecord;
    }

    /**
     * 新增打卡设置
     */
    public void addClockConfig(List<Organization> organizations, String organizationId, String configId) {
        Organization organization = organizationRepository.findById(organizationId).get();
        organization.setClockConfigId(configId);
        organizations.add(organization);
        Iterable<Organization> iterable = organizationRepository.findAll(QOrganization.organization.parent.eq(organizationId));
        if (Objects.nonNull(iterable) && Objects.nonNull(iterable.iterator())) {
            Iterator<Organization> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                addClockConfig(organizations, iterator.next().getId(), configId);
            }
        }
    }

    /**
     * 打卡操作
     *
     * @param request
     * @param operator
     */
    public void clock(CreateClockRecordRequest request, OperatorModel operator) {
        Optional<ClockRecord> clockRecordOptional = clockRecordRepository.findOne(QClockRecord.clockRecord.operator.eq(operator.getId())
                .and(QClockRecord.clockRecord.date.eq(ZWDateUtil.getDate())));
        ClockRecord clockRecord = null;
        if (!clockRecordOptional.isPresent()) {
            clockRecord = createRecord(operator.getOrganization(), ZWDateUtil.getDate(), operator.getId(), operator.getFullName());
        } else {
            clockRecord = clockRecordOptional.get();
        }
        try {
            Date clockTime = ZWDateUtil.getNowDateTime();

            switch (request.getClockType()) {
                case MORNING:
                    mornClock(request, clockRecord, clockRecord.getMorningAllowTimeMin(), clockRecord.getMorningSignTime(), clockRecord.getMorningAllowTimeMax(), clockTime);
                    break;
                case NOON:
                    noonClock(request, clockRecord, clockRecord.getNoonAllowTimeMin(), clockRecord.getNoonSignTime(), clockRecord.getNoonAllowTimeMax(), clockTime);
                    break;
                case AFTERNOON:
                    afterClock(request, clockRecord, clockRecord.getAfterAllowTimeMin(), clockRecord.getAfterSignTime(), clockRecord.getAfterAllowTimeMax(), clockTime);
                    break;
                default:
                    return;
            }
            clockRecordRepository.save(clockRecord);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(null, "", "clock.fail");
        }
    }


    /**
     * 上班打卡
     *
     * @param request
     * @param clockRecord
     * @param minTime
     * @param signTime
     * @param maxTime
     * @param clockTime
     */
    public void mornClock(CreateClockRecordRequest request, ClockRecord clockRecord, Date minTime, Date signTime, Date maxTime, Date clockTime) {
        clockRecord.setMorningClockTime(clockTime);
        clockRecord.setMorningClockAddr(request.getClockAddr());
        if (clockTime.after(minTime) && clockTime.before(maxTime)) {
            if (clockTime.before(maxTime) && clockTime.after(signTime)) {
                clockRecord.setMorningStatus(ClockStatus.LATE);
            } else {
                clockRecord.setMorningStatus(ClockStatus.NORMAL);
            }
        }
    }

    /**
     * 午间打卡
     *
     * @param request
     * @param clockRecord
     * @param minTime
     * @param signTime
     * @param maxTime
     * @param clockTime
     */
    public void noonClock(CreateClockRecordRequest request, ClockRecord clockRecord, Date minTime, Date signTime, Date maxTime, Date clockTime) {
        clockRecord.setNoonClockTime(clockTime);
        clockRecord.setNoonClockAddr(request.getClockAddr());
        if (clockTime.after(minTime) && clockTime.before(maxTime)) {
            clockRecord.setNoonStatus(ClockStatus.NORMAL);
        } else {
            clockRecord.setNoonStatus(ClockStatus.LATE);
        }
    }

    /**
     * 下班打卡
     *
     * @param request
     * @param clockRecord
     * @param minTime
     * @param signTime
     * @param maxTime
     * @param clockTime
     */
    public void afterClock(CreateClockRecordRequest request, ClockRecord clockRecord, Date minTime, Date signTime, Date maxTime, Date clockTime) {
        clockRecord.setAfterClockTime(clockTime);
        clockRecord.setAfterClockAddr(request.getClockAddr());
        if (clockTime.after(minTime) && clockTime.before(maxTime)) {
            if (clockTime.before(signTime) && clockTime.after(minTime)) {
                clockRecord.setAfterStatus(ClockStatus.EARLY);
            } else {
                clockRecord.setAfterStatus(ClockStatus.NORMAL);
            }
        }
    }

    /**
     * 计算当前打卡类型
     *
     * @param record
     * @param recordResponse
     */
    public void getClockType(ClockRecord record, ClockRecordResponse recordResponse) {
        if (!Objects.equals(record.getMorningStatus(), ClockStatus.NORMAL)
                && ZWDateUtil.getNowDateTime().after(record.getMorningAllowTimeMin())
                && ZWDateUtil.getNowDateTime().before(record.getMorningAllowTimeMax())
                && Objects.isNull(record.getMorningClockTime())) {
            recordResponse.setClockType(ClockType.MORNING);
        } else if (!Objects.equals(record.getNoonStatus(), ClockStatus.NORMAL)
                && ZWDateUtil.getNowDateTime().after(record.getNoonAllowTimeMin())
                && ZWDateUtil.getNowDateTime().before(record.getNoonAllowTimeMax())
                && Objects.isNull(record.getNoonClockTime())) {
            recordResponse.setClockType(ClockType.NOON);
        } else if (!Objects.equals(record.getAfterStatus(), ClockStatus.NORMAL)
                && ZWDateUtil.getNowDateTime().after(record.getAfterAllowTimeMin())
                && ZWDateUtil.getNowDateTime().before(record.getAfterAllowTimeMax())
                && Objects.isNull(record.getNoonClockTime())) {
            recordResponse.setClockType(ClockType.AFTERNOON);
        }
    }

}

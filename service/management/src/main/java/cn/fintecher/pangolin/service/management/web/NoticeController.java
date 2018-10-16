package cn.fintecher.pangolin.service.management.web;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.managentment.Notice;
import cn.fintecher.pangolin.service.management.model.request.NoticeCteateRequest;
import cn.fintecher.pangolin.service.management.model.request.NoticeModifyRequest;
import cn.fintecher.pangolin.service.management.model.request.NoticeSearchRequest;
import cn.fintecher.pangolin.service.management.model.response.NoticeResponse;
import cn.fintecher.pangolin.service.management.repository.NoticeRepository;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/api/notice")
@Api(value = "公告管理", description = "公告管理")
public class NoticeController {

    Logger log = LoggerFactory.getLogger(NoticeController.class);

    @Autowired
    NoticeRepository noticeRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    OperatorService operatorService;

    @GetMapping("/getAllNotices")
    @ApiOperation(notes = "获取公告列表", value = "获取公告列表")
    public ResponseEntity<Page<NoticeResponse>> getAllNotices(NoticeSearchRequest request,
                                                              Pageable pageable){
        Page<NoticeResponse> responsePage = noticeRepository.findAll(request.generateQueryBuilder(),pageable).map(notice -> {
            NoticeResponse response = modelMapper.map(notice,NoticeResponse.class);
            return response;
        });
        return ResponseEntity.ok().body(responsePage);
    }

    @GetMapping("/getNoticeList")
    @ApiOperation(notes = "获取公告列表(首页)", value = "获取公告列表(首页)")
    public ResponseEntity<List<NoticeResponse>> getNoticeList(){
        Sort sort = new Sort(Sort.Direction.DESC,"operatorTime");
        Pageable pageable = new PageRequest(0,10,sort);
        List<Notice> responseList = noticeRepository.findAll(pageable).getContent();
        Type type = new TypeToken<List<NoticeResponse>>(){}.getType();
        ModelMapper modelMapper = new ModelMapper();
        List<NoticeResponse> response = modelMapper.map(responseList,type);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/createNotice")
    @ApiOperation(notes = "新建公告", value = "新建公告")
    public ResponseEntity<Void> createNotice(@RequestBody  NoticeCteateRequest request,
                                             @RequestHeader(value = "X-UserToken") String token){
        OperatorModel operatorModel = operatorService.getSessionByToken(token);
        Notice notice = new Notice();
        BeanUtils.copyProperties(request, notice);
        notice.setOperatorName(operatorModel.getFullName());
        notice.setOperatorTime(ZWDateUtil.getNowDateTime());
        noticeRepository.save(notice);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/updateNotice")
    @ApiOperation(notes = "修改公告", value = "修改公告")
    public ResponseEntity<Void> updateNotice(@RequestBody NoticeModifyRequest request,
                                             @RequestHeader(value = "X-UserToken") String token){
        OperatorModel operatorModel = operatorService.getSessionByToken(token);
        Notice notice = new Notice();
        BeanUtils.copyProperties(request, notice);
        notice.setOperatorName(operatorModel.getFullName());
        notice.setOperatorTime(ZWDateUtil.getNowDateTime());
        noticeRepository.save(notice);
        return ResponseEntity.ok().body(null);
    }

    @DeleteMapping("/deleteNotice")
    @ApiOperation(notes = "删除公告", value = "删除公告")
    public ResponseEntity<Void> deleteNotice(@RequestParam String id){

        noticeRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

}


package cn.fintecher.pangolin.service.management.web;

import cn.fintecher.pangolin.common.model.OperatorModel;
import cn.fintecher.pangolin.common.utils.ZWDateUtil;
import cn.fintecher.pangolin.entity.managentment.QSensitiveWord;
import cn.fintecher.pangolin.entity.managentment.SensitiveWord;
import cn.fintecher.pangolin.service.management.model.request.CreateSensitiveWordRequest;
import cn.fintecher.pangolin.service.management.model.request.ModifySensitiveWordRequest;
import cn.fintecher.pangolin.service.management.model.request.SensitiveWordSearchRequest;
import cn.fintecher.pangolin.service.management.model.response.SensitiveWordResponse;
import cn.fintecher.pangolin.service.management.repository.SensitiveWordRepository;
import cn.fintecher.pangolin.service.management.service.OperatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.IterableUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensitiveWord")
@Api(value = "敏感词管理", description = "敏感词管理")
public class SensitiveWordController {

    Logger log = LoggerFactory.getLogger(SensitiveWordController.class);

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SensitiveWordRepository sensitiveWordRepository;

    @Autowired
    OperatorService operatorService;

    @GetMapping("/getAllSensitiveWords")
    @ApiOperation(notes = "获取所有敏感词", value = "获取所有敏感词")
    public ResponseEntity<Page<SensitiveWordResponse>> getAllSensitiveWords(SensitiveWordSearchRequest request,
                                               Pageable pageable){
        log.info("获取所有敏感词{}",request);
        Page<SensitiveWordResponse> page = sensitiveWordRepository.findAll(request.generateQueryBuilder(),pageable)
                .map(sensitiveWord -> {
                    return modelMapper.map(sensitiveWord,SensitiveWordResponse.class);
                }
        );
        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/getSensitiveWordList")
    @ApiOperation(notes = "获取敏感词列表", value = "获取敏感词列表")
    public ResponseEntity<List<SensitiveWord>> getSensitiveWordList(@RequestParam String principalId){
        log.info("获取所有敏感词{}",principalId);
        List<SensitiveWord> sensitiveWords = IterableUtils.toList(sensitiveWordRepository.findAll(QSensitiveWord.sensitiveWord.principalId.eq(principalId)));
        return ResponseEntity.ok().body(sensitiveWords);
    }

    @PostMapping("/createSensitiveWord")
    @ApiOperation(notes = "新增敏感词", value = "新增敏感词")
    public ResponseEntity<Void> createSensitiveWord(@RequestBody CreateSensitiveWordRequest request,
                                                    @RequestHeader(value = "X-UserToken") String token){
        log.info("新增敏感词{}",request);
        OperatorModel operator = operatorService.getSessionByToken(token);
        SensitiveWord sensitiveWord = new SensitiveWord();
        BeanUtils.copyProperties(request, sensitiveWord);
        sensitiveWord.setOperatorName(operator.getFullName());
        sensitiveWord.setOperatorTime(ZWDateUtil.getNowDateTime());
        sensitiveWordRepository.save(sensitiveWord);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/modifySensitiveWord")
    @ApiOperation(notes = "修改敏感词", value = "修改敏感词")
    public ResponseEntity<Void> modifySensitiveWord(@RequestBody ModifySensitiveWordRequest request,
                                                    @RequestHeader(value = "X-UserToken") String token){
        log.info("修改敏感词{}",request);
        OperatorModel operator = operatorService.getSessionByToken(token);
        SensitiveWord sensitiveWord =sensitiveWordRepository.findById(request.getId()).get();
        sensitiveWord.setLevel(request.getLevel());
        sensitiveWord.setWord(request.getWord());
        sensitiveWord.setOperatorName(operator.getFullName());
        sensitiveWord.setOperatorTime(ZWDateUtil.getNowDateTime());
        sensitiveWordRepository.save(sensitiveWord);
        return ResponseEntity.ok().body(null);
    }

    @DeleteMapping("/deleteWordById")
    @ApiOperation(notes = "删除敏感词", value = "删除敏感词")
    public ResponseEntity<Void> deleteWordById(@RequestParam String id){
        log.info("删除敏感词{}",id);
        sensitiveWordRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
    }
}

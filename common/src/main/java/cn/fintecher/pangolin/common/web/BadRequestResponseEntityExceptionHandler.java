package cn.fintecher.pangolin.common.web;

import cn.fintecher.pangolin.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ChenChang on 2018/6/8.
 */

@ControllerAdvice
@RestController
public class BadRequestResponseEntityExceptionHandler {
    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Map<String, ?>> handleBadRequestException(BadRequestException ex, WebRequest request) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> error = new HashMap<>();
        error.put("defaultMessage", messageSource.getMessage(ex.getMessage(), ex.getArgs(), request.getLocale()));
        error.put("objectName", ex.getObjectName());
        error.put("code", "badRequestException");
        list.add(error);
        Map map = new HashMap<>();
        map.put("errors", list);
        map.put("timestamp", new Long(System.currentTimeMillis()));
        map.put("status", HttpStatus.BAD_REQUEST.value());
        map.put("error", "Bad Request");
        map.put("message", messageSource.getMessage(ex.getMessage(), ex.getArgs(), request.getLocale()));
        map.put("path", request.toString());
        return ResponseEntity.badRequest().body(map);
    }


}

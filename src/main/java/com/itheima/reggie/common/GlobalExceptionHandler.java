package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 异常处理器
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 异常处理方法
     *
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        
        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            return R.error(split[2] + "已存在");
        }
        return R.error("未知错误");
    }
    
    /**
     * CustomException异常处理方法
     *
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> runTimeHandler(CustomException ex) {
        
        return R.error(ex.getMessage());
    }
    
    
}


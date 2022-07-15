package org.wg.wiki.controller;

import org.wg.wiki.model.resp.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理器
 */
@ControllerAdvice
public class BindExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BindExceptionHandler.class);

    // 参数校验异常统一处理
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public Result exceptionHandler(BindException e) {
        String message = e.getAllErrors().get(0).getDefaultMessage();
        logger.warn("参数校验失败: {}", message);
        return Result.error(message);
    }
}

package com.hand.bdss.web.common.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.hand.bdss.web.common.vo.BaseResponse;
/**
 * 对异常做统一处理
 * @author youtakeoff
 *
 */
@ControllerAdvice
@ResponseBody
public class ExceptionAOP {
	private static final Logger logger = LoggerFactory.getLogger(ExceptionAOP.class);
	/** 
     * 400 - Bad Request 
     */  
    @ResponseStatus(HttpStatus.BAD_REQUEST)  
    @ExceptionHandler(ValidationException.class)  
    public String handleValidationException(ValidationException e) {  
        logger.error("参数验证失败", e);
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        base.setReturnMessage("参数验证失败");
        return base.toString();  
    }  
	
	/** 
     * 400 - Bad Request 
     */  
    @ResponseStatus(HttpStatus.BAD_REQUEST)  
    @ExceptionHandler(HttpMessageNotReadableException.class)  
    public String handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {  
        logger.error("参数解析失败", e);
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        base.setReturnMessage("参数解析失败");
        return base.toString();  
    }  
  
    /** 
     * 405 - Method Not Allowed 
     */  
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)  
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)  
    public String handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {  
        logger.error("不支持当前请求方法", e); 
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        base.setReturnMessage("不支持当前请求方法");
        return base.toString();  
    }  
  
    /** 
     * 415 - Unsupported Media Type 
     */  
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)  
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)  
    public String handleHttpMediaTypeNotSupportedException(Exception e) {  
        logger.error("不支持当前媒体类型", e);
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        base.setReturnMessage("不支持当前媒体类型");
        return base.toString();  
    }  
  
    /** 
     * 500 - Internal Server Error 
     */  
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  
    @ExceptionHandler(Exception.class)  
    public String handleException(Exception e) {  
        logger.error("服务运行异常", e); 
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        base.setReturnMessage("服务运行异常");
        return base.toString();  
    }  
}

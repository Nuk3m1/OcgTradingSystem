package com.nuk3m1.ocgtradingsystem.exception;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import com.nuk3m1.ocgtradingsystem.common.BaseResponse;
import com.nuk3m1.ocgtradingsystem.common.ResultUtils;
import com.nuk3m1.ocgtradingsystem.enums.ErrorCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.xml.transform.Result;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> BusinessExcptionHandler(BusinessException e){
        return ResultUtils.error(e.getCode(),e.getMessage());

    }
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> RuntimeExceptionHandler(RuntimeException e){
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"系统错误！");
    }

    @ExceptionHandler(NotLoginException.class)
    public BaseResponse<?> notLoginExceptionHandler(RuntimeException e) {
        return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR, "抓获到未登录异常");
    }
    @ExceptionHandler(DisableServiceException.class)
    public BaseResponse<?> DisableServiceExceptionHandler(DisableServiceException e){
        return ResultUtils.error(ErrorCode.OPERATION_ERROR,"该账号被封禁");
    }


}

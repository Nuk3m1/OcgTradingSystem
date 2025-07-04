package com.nuk3m1.ocgtradingsystem.common;

import com.nuk3m1.ocgtradingsystem.enums.ErrorCode;
import com.nuk3m1.ocgtradingsystem.exception.BusinessException;

public class ThrowUtils {
    public static void ThrowIf(boolean condition , RuntimeException runtimeException){
        if(condition){
            throw runtimeException;
        }
    }
    public static void ThrowIf(boolean condition , ErrorCode errorCode){
        ThrowIf(condition , new BusinessException(errorCode));
    }
    public static void ThrowIf(boolean condition , ErrorCode errorCode , String message){
        ThrowIf(condition , new BusinessException(errorCode,message));
    }
}

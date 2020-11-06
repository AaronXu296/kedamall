package com.example.kedamall.product.exception;

import com.example.common.exception.BizCodeEnum;
import com.example.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 集中处理所有异常
 */
@Slf4j
//@ResponseBody
@RestControllerAdvice(basePackages = "com.example.kedamall.product.controller")
//@ControllerAdvice(basePackages = "com.example.kedamall.product.controller")
public class KedamallExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        log.error("数据校验出现异常{}，异常类型{}",e.getMessage(),e.getClass());

        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> map = new HashMap<>();
        bindingResult.getFieldErrors().forEach((item)->{
            String field = item.getField();
            String message = item.getDefaultMessage();
            map.put(field,message);
        });
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(),BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data",map);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        log.error("错误",throwable);
        return R.error(BizCodeEnum.UNKNOW_EXEPTION.getCode(),BizCodeEnum.UNKNOW_EXEPTION.getMsg());
    }
}

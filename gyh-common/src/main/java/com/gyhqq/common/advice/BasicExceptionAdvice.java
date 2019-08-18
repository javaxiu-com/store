package com.gyhqq.common.advice;

import com.gyhqq.common.Exception.GyhException;
import com.gyhqq.common.vo.ExceptionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 是个 aop切面
 */
@ControllerAdvice    //这是个通知,会默认拦截所有的Controller, aop切面->需要pom中导入spring-web
public class BasicExceptionAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> exceptionHandle(RuntimeException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * 自定义通用异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(GyhException.class)
    public ResponseEntity<ExceptionResult> gyhExceptionHandle(GyhException e){
        return ResponseEntity.status(e.getStatus()).body(new ExceptionResult(e));
    }


}

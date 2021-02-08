package com.cdyw.swsw.system.app.handler;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.BindingException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 全局异常处理类:
 * java异常主要两大类：CheckedException 和 RuntimeException
 * 继承ResponseEntityExceptionHandler: Provides handling for standard Spring MVC exceptions
 *
 * @author jovi
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * CheckedException 检查时异常
     *
     * @param ex Exception
     * @return CommonResult<Object>
     */
    @ExceptionHandler(value = {
            IOException.class,
            ClassNotFoundException.class,
            NoSuchMethodException.class,
            SQLException.class
    })
    @ResponseBody
    public CommonResult<Object> handleCheckedException(Exception ex) {
        if (ex instanceof IOException) {
            return new CommonResult<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        } else if (ex instanceof ClassNotFoundException) {
            return new CommonResult<>(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        } else if (ex instanceof NoSuchMethodException) {
            return new CommonResult<>(HttpStatus.METHOD_NOT_ALLOWED.value(), ex.getMessage());
        }
        return new CommonResult<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }

    /**
     * RuntimeException 运行时异常
     *
     * @param ex Exception
     * @return CommonResult<Object>
     */
    @ExceptionHandler(value = {
            NullPointerException.class,
            ArrayIndexOutOfBoundsException.class,
            ClassCastException.class,
            IndexOutOfBoundsException.class,
            NumberFormatException.class,
            IllegalArgumentException.class,
            BindingException.class,
            MyBatisSystemException.class,
            ArithmeticException.class
    })
    @ResponseBody
    public CommonResult<Object> handleRuntimeException(Exception ex) {
        return new CommonResult<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }

    /**
     * 此处为了能够友好的展示异常信息，重写父类的handleExceptionInternal方法，将返回值（原先为 new ResponseEntity<>(body, headers, status)）修改为如下
     *
     * @param ex      ex springmvc相关的异常，比如说get方法不支持这种异常
     * @param body    body
     * @param headers headers
     * @param status  status
     * @param request request
     * @return ResponseEntity
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        return new ResponseEntity<>(new CommonResult<>(status.value(), status.getReasonPhrase()), status);
    }
}

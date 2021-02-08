package com.cdyw.swsw.common.domain.vo.result;

import cn.hutool.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author jovi
 * @date 2020-5-9
 * @apiNote 返回结果的通用类：前段交互、异常结果
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 2XX: generally "OK"
     */
    HTTP_OK(HttpStatus.HTTP_OK, "操作成功"),
    /**
     * 500: Internal Server Error
     */
    HTTP_INTERNAL_ERROR(HttpStatus.HTTP_INTERNAL_ERROR, "操作失败"),
    /**
     * 400: Bad Request
     */
    HTTP_BAD_REQUEST(HttpStatus.HTTP_BAD_REQUEST, "请求错误"),
    /**
     * 404: Not Found
     */
    HTTP_NOT_FOUND(HttpStatus.HTTP_NOT_FOUND, "路径查询失败"),
    /**
     * 405: Method Not Allowed
     */
    HTTP_BAD_METHOD(HttpStatus.HTTP_BAD_METHOD, "处理方法不支持"),
    /**
     * 401: Unauthorized
     */
    HTTP_UNAUTHORIZED(HttpStatus.HTTP_UNAUTHORIZED, "暂未登录或token已经过期"),
    /**
     * 403: Forbidden
     */
    HTTP_FORBIDDEN(HttpStatus.HTTP_FORBIDDEN, "没有相关权限"),
    /**
     * 304: Not Modified
     */
    HTTP_NOT_MODIFIED(HttpStatus.HTTP_NOT_MODIFIED, "未定义参数");

    private final int code;

    private final String message;

}

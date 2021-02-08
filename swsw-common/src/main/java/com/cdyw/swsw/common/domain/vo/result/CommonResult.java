package com.cdyw.swsw.common.domain.vo.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jovi
 * @date 2020-5-9
 * @apiNote 和前端交互的通用类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult<T> {

    private int code;

    private String message;

    private T data;

    public CommonResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 成功返回结果不带参，比如新增、创建等操作
     */
    public static <T> CommonResult<T> success() {
        return new CommonResult<>(ResultCode.HTTP_OK.getCode(), ResultCode.HTTP_OK.getMessage());
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(ResultCode.HTTP_OK.getCode(), ResultCode.HTTP_OK.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<>(ResultCode.HTTP_OK.getCode(), message, data);
    }

    /**
     * 扩展使用
     * 失败返回结果
     *
     * @param errorCode 错误码
     */
    public static <T> CommonResult<T> failed(ResultCode errorCode) {
        return new CommonResult<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 自定义错误消息
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<>(ResultCode.HTTP_INTERNAL_ERROR.getCode(), message, null);
    }

    /**
     * 默认的
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed() {
        return failed(ResultCode.HTTP_INTERNAL_ERROR);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> CommonResult<T> validateFailed() {
        return failed(ResultCode.HTTP_NOT_MODIFIED);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<>(ResultCode.HTTP_NOT_MODIFIED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<>(ResultCode.HTTP_UNAUTHORIZED.getCode(), ResultCode.HTTP_UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<>(ResultCode.HTTP_FORBIDDEN.getCode(), ResultCode.HTTP_FORBIDDEN.getMessage(), data);
    }
}

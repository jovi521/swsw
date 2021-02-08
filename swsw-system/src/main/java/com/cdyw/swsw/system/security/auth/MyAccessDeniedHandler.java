package com.cdyw.swsw.system.security.auth;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.common.domain.vo.result.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限校验处理器
 */
@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private JSONAuthentication jsonAuthentication;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        //输出
        jsonAuthentication.WriteJSON(request, response, new CommonResult<Object>(ResultCode.HTTP_FORBIDDEN.getCode(),
                "权限不足:" + accessDeniedException.getMessage()));
    }
}

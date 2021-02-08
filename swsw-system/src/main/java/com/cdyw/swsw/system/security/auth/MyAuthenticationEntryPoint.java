package com.cdyw.swsw.system.security.auth;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 身份校验失败处理器，如 token 错误
 */
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private JSONAuthentication jsonAuthentication;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        //输出
        jsonAuthentication.WriteJSON(request, response, CommonResult.failed("访问此资源需要完全身份验证（" + authException.getMessage() + "）！"));
    }
}

package com.cdyw.swsw.system.security.auth;


import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 成功退出处理器
 */
@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private JSONAuthentication jsonAuthentication;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
//        UserDetails user = (UserDetails) authentication.getPrincipal();
//        String username = user.getUsername();

        jsonAuthentication.WriteJSON(request, response, CommonResult.success("退出成功"));
    }
}

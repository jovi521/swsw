package com.cdyw.swsw.system.security.auth;


import cn.hutool.core.util.StrUtil;
import com.cdyw.swsw.system.app.config.UsernameOrPasswordError;
import com.cdyw.swsw.system.app.config.VerifyCodeError;
import com.cdyw.swsw.system.security.constant.Constants;
import com.cdyw.swsw.system.security.util.RedisCache;
import com.cdyw.swsw.system.service.user.SysUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

/**
 * 重写UsernamePasswordAuthenticationFilter过滤器
 */
public class MyUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private SysUserService userService;
    @Autowired
    private RedisCache redisCache;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        if (request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_UTF8_VALUE)
                || request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {

            ObjectMapper mapper = new ObjectMapper();
            UsernamePasswordAuthenticationToken authRequest = null;
            //取authenticationBean
            Map<String, String> authenticationBean = null;
            //用try with resource，方便自动释放资源
            try (InputStream is = request.getInputStream()) {
                authenticationBean = mapper.readValue(is, Map.class);
            } catch (IOException e) {
                //将异常放到自定义的异常类中
                throw new MyAuthenticationException(e.getMessage());
            }
            try {
                if (!authenticationBean.isEmpty()) {
                    //获得账号、密码
                    String username = authenticationBean.get(SPRING_SECURITY_FORM_USERNAME_KEY);
                    String password = authenticationBean.get(SPRING_SECURITY_FORM_PASSWORD_KEY);
                    String uuid = authenticationBean.get("uuid");
                    String verifyCode = authenticationBean.get("code");
                    if (StrUtil.isEmpty(username) || StrUtil.isEmpty(password)) {
                        throw new UsernameOrPasswordError("用户名或密码为空。。");
                    }
                    if (StrUtil.isEmpty(uuid) || StrUtil.isEmpty(verifyCode)) {
                        throw new VerifyCodeError("验证码为空。。");
                    }
                    // 检查验证码是否有误
                    // 待检查的验证码
                    String verifiedCodeKey = Constants.CAPTCHA_CODE_KEY + uuid;
                    String verifiedCode = redisCache.getCacheObject(verifiedCodeKey);
                    if (!Objects.equals(verifiedCode, verifyCode)) {
                        throw new VerifyCodeError("验证码有误。。");
                    }
                    //检测账号、密码是否存在
                    if (userService.checkLogin(username, password)) {
                        //将账号、密码装入UsernamePasswordAuthenticationToken中
                        authRequest = new UsernamePasswordAuthenticationToken(username, password);
                        setDetails(request, authRequest);
                        return this.getAuthenticationManager().authenticate(authRequest);
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
                throw new MyAuthenticationException(e.getMessage());
            }
            return null;
        } else {
            return this.attemptAuthentication(request, response);
        }
    }
}

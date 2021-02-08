package com.cdyw.swsw.system.security.auth;


import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.security.config.AuthUser;
import com.cdyw.swsw.system.security.config.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录成功操作
 */
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String PREFIX_TOKEN = "token-";
    @Autowired
    private JSONAuthentication jsonAuthentication;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        //取得账号信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //
//        System.out.println("userDetails = " + userDetails);
        //取token
        //好的解决方案，登录成功后token存储到数据库中
        //只要token还在过期内，不需要每次重新生成
        //先去缓存中找
        // String token = TokenCache.getTokenFromCache(PREFIX_TOKEN + userDetails.getUsername());
        String token = (String) redisTemplate.opsForValue().get(PREFIX_TOKEN + userDetails.getUsername());
        if (token == null) {
//            System.out.println("初次登录，token还没有，生成新token。。。。。。");
            //如果token为空，则去创建一个新的token
            AuthUser authUser = (AuthUser) userDetails;
            token = jwtTokenUtil.generateToken(userDetails, authUser.getUserEntity());
            //把新的token存储到缓存中
            // TokenCache.setToken(userDetails.getUsername(), token);
            redisTemplate.opsForValue().set(PREFIX_TOKEN + userDetails.getUsername(), token, 30, TimeUnit.MINUTES);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("username", userDetails.getUsername());
        map.put("auth", userDetails.getAuthorities());
        map.put("token", token);
        //输出
        jsonAuthentication.WriteJSON(request, response, CommonResult.success(map));

    }
}

package com.cdyw.swsw.system.security.auth;

import com.cdyw.swsw.common.domain.entity.api.SysApi;
import com.cdyw.swsw.system.dao.api.SysApiMapper;
import com.cdyw.swsw.system.security.config.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class DynamicPermission {

    @Autowired
    private SysApiMapper sysApiMapper;


    /**
     * 判断有访问API的权限
     *
     * @param request
     * @param authentication
     * @return
     * @throws MyaccessDeniedException
     */
    public boolean checkPermisstion(HttpServletRequest request,
                                    Authentication authentication) throws MyaccessDeniedException {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        //当前访问路径
        String temRequestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String requestURI = temRequestURI.replace(contextPath, "");
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {

            UserDetails userDetails = (UserDetails) principal;
            AuthUser authUser = (AuthUser) userDetails;
            //得到当前的账号
            // String username = userDetails.getUsername();
            // 这里可以尝试从token中取userid
            Integer userId = authUser.getUserEntity().getUserId();
            //Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();
            // System.out.println("DynamicPermission  username = " + username);
            // 通过账号获取资源鉴权
            // List<SysApi> apiUrls = service.getApiUrlByUserid(username);
            List<SysApi> apiUrls = sysApiMapper.getApiUrlByUserid(userId);
//            if(!StrUtil.isEmpty(requestURI) && requestURI.contains(contextPath)){
//                requestURI = requestURI.substring(requestURI.indexOf("/"), requestURI.indexOf(contextPath));
//            }
            //提交类型
            String urlMethod = request.getMethod();
            // System.out.println("DynamicPermission requestURI = " + requestURI);
            //判断当前路径中是否在资源鉴权中
            boolean rs = apiUrls.stream().anyMatch(item -> {
                String apiUrl = item.getApiUrl();
                //判断URL是否匹配
                boolean hashAntPath = antPathMatcher.match(apiUrl, requestURI);
//                boolean hashAntPath = requestURI.contains(apiUrl);
                //判断请求方式是否和数据库中匹配（数据库存储：GET,POST,PUT,DELETE）
                String dbMethod = item.getApiMethod();
                //处理null，万一数据库存值
                dbMethod = (dbMethod == null) ? "" : dbMethod;
                int hasMethod = dbMethod.indexOf(urlMethod);
//                System.out.println("hashAntPath = " + hashAntPath);
//                System.out.println("hasMethod = " + hasMethod);
//                System.out.println("hashAntPath && hasMethod = " + (hashAntPath && hasMethod != -1));
                //两者都成立，返回真，否则返回假
                return hashAntPath && (hasMethod != -1);
            });
            //返回
            if (rs) {
                return rs;
            } else {
                throw new MyaccessDeniedException("您没有访问该API的权限！");
            }
        } else if ("anonymousUser".equals(principal)) {
            int sysPid = 9;
            List<SysApi> apiUrlList = sysApiMapper.getApiUrlByPid(sysPid);
            //判断当前路径中是否在资源鉴权中
            boolean result = apiUrlList.stream().anyMatch(item -> {
                String apiUrl = item.getApiUrl();
                //判断URL是否匹配
                boolean hashAntPath = antPathMatcher.match(apiUrl, requestURI);
//                System.out.println("hashAntPath = " + hashAntPath);
                //两者都成立，返回真，否则返回假
                return hashAntPath;
            });
            //返回
            if (result) {
                return result;
            } else {
                throw new MyaccessDeniedException("404, notfound");
            }
        } else {
            throw new MyaccessDeniedException("不是UserDetails类型！");
        }
    }
}


























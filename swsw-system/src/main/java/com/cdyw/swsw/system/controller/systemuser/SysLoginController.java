package com.cdyw.swsw.system.controller.systemuser;


import com.cdyw.swsw.common.domain.entity.user.SysUserEntity;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.security.config.JwtTokenUtil;
import com.cdyw.swsw.system.security.constant.Constants;
import com.cdyw.swsw.system.service.user.SysRoleTableService;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录验证
 *
 * @author ruoyi
 */
@RestController
public class SysLoginController {

    @Autowired
    private SysRoleTableService sysRoleTableService;

    /**
     * 令牌自定义标识
     */
    @Value("${token.header}")
    private String header;

    @Autowired
    private JwtTokenUtil tokenUtil;

//    private final TokenService tokenService;
//
//    @Autowired
//    public SysLoginController(TokenService tokenService) {
//        this.tokenService = tokenService;
//    }

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
//    @PostMapping("/login")
//    public CommonResult<?> login(@RequestBody LoginBody loginBody) {
//        // 生成令牌
//        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
//                loginBody.getUuid());
//        HashMap<String, Object> resultMap = new HashMap<>();
//        resultMap.put("token", token);
//        return CommonResult.success(resultMap);
//    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    public CommonResult<?> getInfo(HttpServletRequest request) throws Exception {
        // 从请求头中获取token
        String token = request.getHeader(header);
        if (StringUtils.isEmpty(token)) {
            throw new RuntimeException("没有携带token..");
        }
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        // 解析token
        Claims claims = tokenUtil.getClaimsFromToken(token);
        SysUserEntity user = new SysUserEntity();
        Map userMap = (LinkedHashMap) claims.get(JwtTokenUtil.CLAIM_USER);
        String userNameStr = (String) userMap.get("userName");
        String nickNameStr = (String) userMap.get("nickName");
        Integer stateInt = (Integer) userMap.get("state");
        String descriptionStr = (String) userMap.get("description");
        Integer userId = (Integer) userMap.get("userId");
        user.setUserId(userId);
        user.setUserName(userNameStr);
        user.setNickName(nickNameStr);
        user.setState(stateInt);
        user.setDescription(descriptionStr);
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("user", user);
        List<String> roleList = sysRoleTableService.getRolesByUserName(userNameStr);
        if (roleList != null && roleList.size() > 0) {
            user.setRoleList(roleList);
        } else {
            throw new RuntimeException("该用户暂未分配角色，请联系管理员！");
        }
        return CommonResult.success(resultMap);
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
//    @GetMapping("getRouters")
//    public AjaxResult<?> getRouters() {
//        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
//        // 用户信息
//        SysUser user = loginUser.getUser();
//        List<SysMenu> menus = menuService.selectMenuTreeByUserId(user.getUserId());
//        return AjaxResult.success(menuService.buildMenus(menus));
//    }
}

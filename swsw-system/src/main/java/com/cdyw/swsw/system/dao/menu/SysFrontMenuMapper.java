package com.cdyw.swsw.system.dao.menu;

import com.cdyw.swsw.common.domain.entity.menu.FrontMenu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 前台菜单
 *
 * @author cdyw
 */
@Repository
public interface SysFrontMenuMapper {

    /**
     * 插入前台菜单
     *
     * @param frontMenu
     * @param menuId
     */
    void insertMenu(@Param("frontMenu") FrontMenu frontMenu, @Param("menuId") Integer menuId);

    /**
     * 查询所有前台菜单
     *
     * @return
     */
    List<Map<String, Object>> selectFrontMenu();
}

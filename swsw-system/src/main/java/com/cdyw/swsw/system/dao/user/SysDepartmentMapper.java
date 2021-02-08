package com.cdyw.swsw.system.dao.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface SysDepartmentMapper {

    List<HashMap<String, Object>> selectDepartmentAll();
}

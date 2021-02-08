package com.cdyw.swsw.common.domain.entity.cldas;

import com.cdyw.swsw.common.domain.dto.ds.CimissDsResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Cldas1km cimiss 获取结果集
 *
 * @author jovi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Cldas1km extends CimissDsResult {

    /**
     * 产品类别 eg : HRCLDAS
     */
    private String prodCate;
    /**
     * 产品内容 eg : QAIR
     */
    private String prodCont;
    /**
     * 产品系统 eg :
     */
    private String prodSys;
}

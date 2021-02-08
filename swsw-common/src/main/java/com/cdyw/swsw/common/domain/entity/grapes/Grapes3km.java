package com.cdyw.swsw.common.domain.entity.grapes;

import com.cdyw.swsw.common.domain.dto.ds.CimissDsResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Grapes3km cimiss 获取结果集
 *
 * @author jovi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Grapes3km extends CimissDsResult {

    /**
     * 产品类别 eg : NWPC
     */
    private String prodCate;
    /**
     * 产品内容 eg : 3KM
     */
    private String prodCont;
    /**
     * 产品系统 eg : GRAPES
     */
    private String prodSys;
}

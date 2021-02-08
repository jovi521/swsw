package com.cdyw.swsw.system.app.job.product;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.product.DataProductService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author jovi
 */
@Component
public class DataProductTestJobHandler extends IJobHandler {

    private final DataProductService dataProductService;

    @Autowired
    public DataProductTestJobHandler(DataProductService dataProductService) {
        this.dataProductService = dataProductService;
    }

    /**
     * 定时每天创建产品文档所需要的测试数据
     *
     * @param s String
     * @return ReturnT
     */
    @XxlJob(value = "dataProductTestJobHandler")
    @Override
    public ReturnT<String> execute(String s) throws IOException, InterruptedException {
        CommonResult<?> insert = dataProductService.insert();
        if (insert.getData() != null) {
            return ReturnT.SUCCESS;
        }
        return ReturnT.FAIL;
    }
}

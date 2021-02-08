package com.cdyw.swsw.data.common.interceptor;

import com.cdyw.swsw.common.common.component.CommonUrlParam;
import com.cdyw.swsw.common.domain.ao.enums.CommonParamKeyEnum;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 通用 URL　参数
 *
 * @author jovi
 */
@Component
public class CommonUrlParamInterceptor extends BasePathMatchInterceptor {

    private final CommonUrlParam commonUrlParam;

    public CommonUrlParamInterceptor(CommonUrlParam commonUrlParam) {
        this.commonUrlParam = commonUrlParam;
    }

    @Override
    protected Response doIntercept(Chain chain) throws IOException {
        Request request = chain.request();
        // request 不需要添加 header 信息，提取公共部分绑定在公共 URL 上
        HttpUrl url = request.url();
        // 重新组装公共 URL
        HttpUrl newUrl = url.newBuilder()
                // 由于用户名和密码是动态的，所以不能写成公共的参数引入
//                .addQueryParameter(CommonParamKeyEnum.PARAM_KEY_USER_ID.getParamKey(), commonUrlParam.getUserId1())
//                .addQueryParameter(CommonParamKeyEnum.PARAM_KEY_PWD.getParamKey(), commonUrlParam.getPwd1())
                .addQueryParameter(CommonParamKeyEnum.PARAM_KEY_DATA_FORMAT.getParamKey(), commonUrlParam.getDataFormat())
                .addQueryParameter(CommonParamKeyEnum.PARAM_KEY_LIMIT_CNT.getParamKey(), commonUrlParam.getLimitCnt())
                .build();
        // 重新组装公共 request
        Request newRequest = request.newBuilder()
                .url(newUrl)
                .build();
        return chain.proceed(newRequest);
    }
}

package com.cdyw.swsw.data.common.http;

import com.cdyw.swsw.data.common.interceptor.CommonUrlParamInterceptor;
import com.github.lianjiatech.retrofit.spring.boot.annotation.Intercept;
import com.github.lianjiatech.retrofit.spring.boot.annotation.OkHttpClientBuilder;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.LogStrategy;
import okhttp3.OkHttpClient;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 所有远程调用 Http 服务全在这里处理(Cimiss远程接口调用)
 *
 * @author jovi
 */
@Component
@RetrofitClient(baseUrl = "${common.param.baseUrl}", poolName = "pool1", logLevel = Level.ERROR, logStrategy = LogStrategy.BODY)
@Intercept(handler = CommonUrlParamInterceptor.class, include = {"/cimiss-web/**"}, exclude = {"/test"})
public interface HttpApi {

    /**
     * 自定义OkHttpClient
     *
     * @return OkHttpClient.Builder
     */
    @OkHttpClientBuilder
    static OkHttpClient.Builder okhttpClientBuilder() {
        return new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS);
    }

    /**
     * 通用接口
     *
     * @param paramsMap 公共参数 Map
     * @param userId    用户名
     * @param pwd       密码
     * @return CommonResult
     */
    @GET("api")
    Map<String, Object> getResult(@QueryMap Map<String, Object> paramsMap, @Query("userId") String userId, @Query("pwd") String pwd);
}

package com.cdyw.swsw.data.common.http;

import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.LogStrategy;
import okhttp3.ResponseBody;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 所有远程调用 Http 下载服务全在这里处理(Cimiss远程接口调用)
 *
 * @author jovi
 */
@Component
@RetrofitClient(baseUrl = "${common.download.baseUrl}", poolName = "pool2", logLevel = Level.ERROR, logStrategy = LogStrategy.BODY)
public interface HttpDownloadApi {

    /**
     * 通用下载接口
     * 加 @Streaming 用于大文件下载
     *
     * @param url 公共下载参数
     * @return Call<ResponseBody>
     */
    @Streaming
    @GET
    Call<ResponseBody> downloadResult(@Url String url);
}

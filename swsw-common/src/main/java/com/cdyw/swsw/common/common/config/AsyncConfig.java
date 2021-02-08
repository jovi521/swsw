package com.cdyw.swsw.common.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jovi
 */
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Bean("getAsyncExecutor")
    @Override
    public Executor getAsyncExecutor() {
        //定义线程池
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //设置核心线程数
        threadPoolTaskExecutor.setCorePoolSize(16);
        //设置线程池最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(20);
        //设置线程队列最大线程数
        threadPoolTaskExecutor.setQueueCapacity(2000);
        //配置线程池中的线程的名称前缀
        threadPoolTaskExecutor.setThreadNamePrefix("async-service-");
        //rejection-policy：当pool已经达到max size的时候，如何处理新任务
        //CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //初始化线程池
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}

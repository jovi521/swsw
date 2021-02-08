package com.cdyw.swsw.common.common.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.thread.ThreadUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * 线程池
 *
 * @author jovi
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    /**
     * 消费队列线程
     *
     * @return ExecutorService
     */
    @Bean(value = "buildConsumerQueueThreadPool")
    public Executor buildConsumerQueueThreadPool() {
        // 利用Hutool的线程工具类先创建线程Factory
        ThreadFactoryBuilder threadFactoryBuilder = ThreadUtil.createThreadFactoryBuilder();

        ThreadFactory namedThreadFactory = threadFactoryBuilder.setNamePrefix("consumer-queue-thread-").build();

        ThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(5, namedThreadFactory);
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        // 2 * cpu/2 * cpu + 1
        threadPoolExecutor.setMaximumPoolSize(5);
        threadPoolExecutor.setKeepAliveTime(0L, TimeUnit.MILLISECONDS);
        threadPoolExecutor.setRejectedExecutionHandler(handler);
        // 队列BlockingQueue默认使用 DelayedWorkQueue();
        return threadPoolExecutor;
    }

    /**
     * 异步线程池
     *
     * @return Executor
     */
    @Bean(value = "asyncServiceExecutor")
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(20);
        //配置最大线程数
        executor.setMaxPoolSize(30);
        //配置队列大小
        executor.setQueueCapacity(99999);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("async-service-");

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}

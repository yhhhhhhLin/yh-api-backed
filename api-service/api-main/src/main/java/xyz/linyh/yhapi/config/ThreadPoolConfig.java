package xyz.linyh.yhapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "genSourceTableExecutor")
    public ThreadPoolExecutor genSourceTableExecutor() {
        ThreadPoolExecutor service = new ThreadPoolExecutor(1, 2, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), new ThreadFactory() {
            private AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "GEN_SOURCE_TABLE_EXECUTOR_" + num.getAndIncrement());
            }
        });
        return service;
    }
}
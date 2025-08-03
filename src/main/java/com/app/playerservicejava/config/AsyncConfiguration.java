package com.app.playerservicejava.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Bean(name = "playerTaskExecutor")
    public Executor playerTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);           // Minimum threads
        executor.setMaxPoolSize(20);           // Maximum threads
        executor.setQueueCapacity(100);        // Queue size for tasks
        executor.setThreadNamePrefix("Player-"); // Thread name prefix
        executor.setKeepAliveSeconds(60);      // Keep alive time for idle threads
        executor.initialize();
        return executor;
    }

    @Bean(name = "paginatedTaskExecutor")
    public Executor paginatedTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);           // Smaller pool for pagination
        executor.setMaxPoolSize(10);           // Max threads for pagination
        executor.setQueueCapacity(50);         // Queue size
        executor.setThreadNamePrefix("Paginated-"); // Thread name prefix
        executor.setKeepAliveSeconds(30);      // Keep alive time
        executor.initialize();
        return executor;
    }
} 
package com.dsa360.api.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class BeanConfig {

	@Bean
	 ModelMapper modelMapper() {
		return new ModelMapper();
	}

    @Bean(name = "asyncExecutor")
    TaskExecutor asyncExecutor() {
		var executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10); 
		executor.setMaxPoolSize(10); 
		executor.setQueueCapacity(25); 
		executor.setThreadNamePrefix("Async-Executor-");
		executor.initialize();
		return executor;
	}

}

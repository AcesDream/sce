package com.study.sce.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.sleuth.instrument.messaging.TraceChannelInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @EnableEurekaClient 添加eureka client支持
 * @EnableHystrix 添加hystrix支持
 *
 * @author lxy
 */
@EnableHystrix
@EnableEurekaClient
@SpringBootApplication
public class EurekaClientaApplication {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


	public static void main(String[] args) {
		TraceChannelInterceptor a = null;
		SpringApplication.run(EurekaClientaApplication.class, args);
	}

}


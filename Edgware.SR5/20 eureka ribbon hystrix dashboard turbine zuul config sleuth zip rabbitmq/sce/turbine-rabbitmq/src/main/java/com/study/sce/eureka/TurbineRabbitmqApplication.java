package com.study.sce.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @EnableEurekaClient 添加eureka client支持
 * @EnableHystrixDashboard 开启hystrix 监控支持
 * @EnableHystrix 添加hystrix支持
 *
 * @author lxy
 */
@EnableTurbineStream
@EnableEurekaClient
@SpringBootApplication
public class TurbineRabbitmqApplication {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


	public static void main(String[] args) {
		SpringApplication.run(TurbineRabbitmqApplication.class, args);
	}

}

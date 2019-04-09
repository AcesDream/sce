package com.study.sce.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * @EnableEurekaClient 添加eureka client支持
 *
 * @author lxy
 */
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class EurekaClientbApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientbApplication.class, args);
	}

}

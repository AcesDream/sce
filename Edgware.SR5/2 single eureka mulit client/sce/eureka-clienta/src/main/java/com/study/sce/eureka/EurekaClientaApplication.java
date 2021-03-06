package com.study.sce.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @EnableEurekaClient 添加eureka client支持
 *
 * @author lxy
 */
@EnableEurekaClient
@SpringBootApplication
public class EurekaClientaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientaApplication.class, args);
	}

}

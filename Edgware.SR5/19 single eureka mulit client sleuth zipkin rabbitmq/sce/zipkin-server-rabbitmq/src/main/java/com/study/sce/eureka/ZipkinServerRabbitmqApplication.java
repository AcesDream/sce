package com.study.sce.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;

/**
 * @EnableEurekaClient 添加eureka client支持
 *
 * @author lxy
 */
@EnableEurekaClient
@EnableZipkinStreamServer
@SpringBootApplication
public class ZipkinServerRabbitmqApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZipkinServerRabbitmqApplication.class, args);
	}

}

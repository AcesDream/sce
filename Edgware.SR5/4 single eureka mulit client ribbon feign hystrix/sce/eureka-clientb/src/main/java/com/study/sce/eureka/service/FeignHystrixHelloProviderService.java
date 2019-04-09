package com.study.sce.eureka.service;

import org.springframework.stereotype.Service;

@Service
public class FeignHystrixHelloProviderService implements FeignHelloProviderService {

	@Override
	public String getHelloMessage(String name) {
		return "feign hystrix return : " + name;
	}
}

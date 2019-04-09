package com.study.sce.eureka.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * feign集成hystrix
 * @author lxy
 *
 */
@FeignClient(name = "eureka-clientc", fallback = FeignHystrixHelloProviderService.class)
public interface FeignHelloProviderService {

	@GetMapping("/clientc/hello/{name}")
	public String getHelloMessage(@PathVariable("name") String name);
}

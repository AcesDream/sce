package com.study.sce.eureka.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * ribbon调用微服务
 *
 * @author lxy
 */
@RefreshScope
@RestController
@RequestMapping("/ribbon")
public class ClientaController {

	@Value("${clienta.secretInfo}")
	private String secretInfo;

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/hello/{name}")
	@HystrixCommand(fallbackMethod = "getHelloMesaageHystrix")
	public String getHelloMessage(@PathVariable("name") String name) {

		return this.secretInfo + ": ribbon return: " + restTemplate.getForObject("http://eureka-clientd/clientd/hello/" + name, String.class);
	}

	public String getHelloMesaageHystrix(String name) {
		return "ribbon hystrix return: " + name;
	}
}



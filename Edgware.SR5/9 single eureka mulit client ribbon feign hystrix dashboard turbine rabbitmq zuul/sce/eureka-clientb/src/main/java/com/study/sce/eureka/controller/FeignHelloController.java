package com.study.sce.eureka.controller;

import com.study.sce.eureka.service.FeignHelloProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign")
public class FeignHelloController {

	@Autowired
	private FeignHelloProviderService feignHelloProviderService;

	@GetMapping("/hello/{name}")
	public String getHelloMessage(@PathVariable("name") String name) {

		return "feign call: " + feignHelloProviderService.getHelloMessage(name);
	}
}

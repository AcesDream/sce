package com.study.sce.eureka.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * 微服务提供者
 *
 * @author lxy
 *
 *
 */
@RestController
@RequestMapping("/clientc")
public class ClientcController {

	@GetMapping("/hello/{name}")
	public String getHelloMessage(@PathVariable("name") String name) {

		return "clientc say hello to: " + name;

	}
}

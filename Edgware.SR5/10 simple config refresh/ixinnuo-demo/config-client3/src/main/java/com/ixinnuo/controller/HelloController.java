package com.ixinnuo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope //开启更新功能
@RequestMapping("/client3")
public class HelloController {

	@Value("${app.info}")
	private String appInfo;

	/**
	 * 返回配置文件中的值
	 */
	@GetMapping("/hello")
	@ResponseBody
	public String getAppInfo(){
		return appInfo;
	}

}

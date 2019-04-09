本工程主要用来演示配置ribbon feign访问微服务，并添加hystrix支持


#### 环境信息
##### 基本环境
+ spring boot 版本：1.5.19.RELEASE
+ spring cloud 版本：Edgware.SR5
+ jdk版本：1.8.0_131

##### 微服务规划
+ 注册中心：eureka
+ 微服务：eureka-clianta、eureka-clientb、eureka-clientc、eureka-clientd、hystrix-dashboard


所有微服务，都注册在注册中心eureka

1. eureka-clienta、eureka-clientd 通过ribbon的方式，访问eureka-clientc提供的微服务；
2. hystrix-dashboard可以监控调用情况

#### hystrix-dashboard的搭建

```xml
<!-- hystrix dashboard依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
</dependency>
```

开启监控支持`@EnableHystrixDashboard`
```java
@EnableHystrixDashboard
@EnableEurekaClient
@SpringBootApplication
public class HystrixDashboardApplication {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


	public static void main(String[] args) {
		SpringApplication.run(HystrixDashboardApplication.class, args);
	}

}
```

#### 测试
启动eureka、eureka-clienta、eureka-clientc、eureka-clientd、hystrix-dashboard

1. 访问`  http://localhost:9200/hystrix`

输入监控地址：`http://localhost:9001/ribbon/hello/lxy`

2. 访问`http://localhost:9001/ribbon/hello/lxy`，观察监视页面
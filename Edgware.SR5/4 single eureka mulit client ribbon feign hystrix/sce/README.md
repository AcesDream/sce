本工程主要用来演示配置ribbon feign访问微服务，并添加hystrix支持


#### 环境信息
##### 基本环境
+ spring boot 版本：1.5.19.RELEASE
+ spring cloud 版本：Edgware.SR5
+ jdk版本：1.8.0_131

##### 微服务规划
+ 注册中心：eureka
+ 微服务：eureka-clianta、eureka-clientb、eureka-clientc


三个微服务，都注册在注册中心eureka

1. eureka-clienta通过ribbon的方式，访问eureka-clientc提供的微服务；
2. eureka-clientb通过feign的方式，访问eureka-clientc提供的微服务；



#### eureka-clienta ribbon 集成hystrix
添加hystrix依赖
```xml
<!-- hystrix依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
```

添加注解`@EnableHystrix`，开启hystrix支持

```java
@EnableHystrix
@EnableEurekaClient
@SpringBootApplication
public class EurekaClientaApplication {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


	public static void main(String[] args) {
		SpringApplication.run(EurekaClientaApplication.class, args);
	}

}
```
定义断路器方法
```java
@RestController
@RequestMapping("/ribbon")
public class RibbonController {


	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/hello/{name}")
	@HystrixCommand(fallbackMethod = "getHelloMesaageHystrix")
	public String getHelloMessage(@PathVariable("name") String name) {

		return "ribbon return: " + restTemplate.getForObject("http://eureka-clientc/clientc/hello/" + name, String.class);
	}

	public String getHelloMesaageHystrix(String name) {
		return "ribbon hystrix return: " + name;
	}
}
```


###### 测试验证
启动eureka、eureka-clientc、eureka-clienta

1. 访问`http://localhost:9001/ribbon/hello/lxy`
2. 关闭eureka-clientc，再次访问`http://localhost:9001/ribbon/hello/lxy`


#### eureka-clientb feign集成hystrix

eureka-clientb添加hystrix依赖
```xml
<!-- hystrix依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
```

定义feign客户端的断路器hystrix实现
```java
@Service
public class FeignHystrixHelloProviderService implements FeignHelloProviderService {

	@Override
	public String getHelloMessage(String name) {
		return "feign hystrix return : " + name;
	}
}
```

为feign客户端添加断路器
```java
@FeignClient(name = "eureka-clientc", fallback = FeignHystrixHelloProviderService.class)
public interface FeignHelloProviderService {

	@GetMapping("/clientc/hello/{name}")
	public String getHelloMessage(@PathVariable("name") String name);
}

```

为feign客户端添加断路器支持，添加注解`@EnableHystrix`
```java
@EnableHystrix
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class EurekaClientbApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientbApplication.class, args);
	}

}
```

调整配置文件，开启feign的hystrix的支持
```yaml
feign:
  hystrix:
    enabled: true
```

###### 测试验证
启动eureka、eureka-clientc、eureka-clientb

1. 访问`http://localhost:9002/feign/hello/lxy`
2. 关闭eureka-clientc，再次访问`http://localhost:9002/feign/hello/lxy`

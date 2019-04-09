本工程主要用来演示配置ribbon feign访问微服务


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




三个服务都添加spring boot web 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

```

#### eureka-clientc定义微服务
```java
@RestController
@RequestMapping("/clientc")
public class HelloProviderController {

	@GetMapping("/hello/{name}")
	public String getHelloMessage(@PathVariable("name") String name) {

		return "clientc say hello to: " + name;

	}
}
```

#### eureka-clienta 使用ribbon方式访问微服务
在eureka-clienta中定义restTemplate，重点是注解`@LoadBalanced`

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

通过ribbon的方式调用微服务
```java
@GetMapping("/hello/{name}")
public String getHelloMessage(@PathVariable("name") String name) {

    return "ribbon call: " + restTemplate.getForObject("http://eureka-clientc/clientc/hello/" + name, String.class);
}
```

访问如下地址进行验证：http://localhost:9001/ribbon/hello/lxy

#### eureka-clientb 使用 feign方式访问微服务

eureka-clientb添加feign依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
```

定义feign客户端
```java
@FeignClient(name = "eureka-clientc")
public interface FeignHelloProviderService {

	@GetMapping("/clientc/hello/{name}")
	public String getHelloMessage(@PathVariable("name") String name);
}
```

添加feign支持注解
```java
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class EurekaClientbApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientbApplication.class, args);
	}

}
```
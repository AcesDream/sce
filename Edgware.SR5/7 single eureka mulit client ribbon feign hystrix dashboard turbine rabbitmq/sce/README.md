本工程使用turbine 集成 rabbitmq 聚合 hytrix监控

#### 环境信息
##### 基本环境
+ spring boot 版本：1.5.19.RELEASE
+ spring cloud 版本：Edgware.SR5
+ jdk版本：1.8.0_131

##### 微服务规划
+ 注册中心：eureka
+ 微服务：eureka-cliante、eureka-clientf、eureka-clientc、hystrix-dashboard、turbine-rabbitmq


所有微服务，都注册在注册中心eureka

1. eureka-cliente、eureka-clientf 通过ribbon的方式，访问eureka-clientc提供的微服务；
2. turbine通过rabbitmq 聚合多个hytrix的信息
3. hystrix-dashboard可以监控turbine，实现监控多个微服务的调用情况


#### eureka-cliente、eureka-clientf的搭建
添加依赖
```xml
<!-- turbine 使用 rabbitmq 聚合 hystrix -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-netflix-hystrix-stream</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>
```

配置文件调整：
```yaml
server:
  port: 9005

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

spring:
  application:
    name: eureka-cliente
  rabbitmq:
      host: localhost
      port: 5672
      username: guest
      password: guest
```

#### turbine-rabbitmq的搭建
添加依赖：
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-turbine-stream</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>
```

配置文件说明：
```yaml
server:
  port: 9400

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

spring:
  application:
    name: turbine-rabbitmq
  rabbitmq:
      host: localhost
      port: 5672
      username: guest
      password: guest

```

开启监控支持`@EnableTurbine`
```java
@EnableTurbineStream
@EnableEurekaClient
@SpringBootApplication
public class TurbineRabbitmqApplication {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


	public static void main(String[] args) {
		SpringApplication.run(TurbineRabbitmqApplication.class, args);
	}

}
```

#### 测试
启动eureka、eureka-cliente、eureka-clientf、hystrix-dashboard、turbine-rabbitmq

1. 访问`  http://localhost:9200/hystrix`

输入监控地址：`http://localhost:9400/turbine.stream`

3. 访问`http://localhost:9005/cliente/hello/lxy`，观察监视页面

4. 访问`http://localhost:9006/clientf/other/lxy`，观察监视页面

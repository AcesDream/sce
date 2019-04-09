本工程使用turbine 聚合 hytrix监控

#### 环境信息
##### 基本环境
+ spring boot 版本：1.5.19.RELEASE
+ spring cloud 版本：Edgware.SR5
+ jdk版本：1.8.0_131

##### 微服务规划
+ 注册中心：eureka
+ 微服务：eureka-clianta、eureka-clientb、eureka-clientc、eureka-clientd、hystrix-dashboard、turbine


所有微服务，都注册在注册中心eureka

1. eureka-clienta、eureka-clientd 通过ribbon的方式，访问eureka-clientc提供的微服务；
2. turbine聚合多个hytrix的信息，
3. hystrix-dashboard可以监控turbine，实现监控多个微服务的调用情况


#### hystrix-dashboard-turbine的搭建

```xml
<!-- turbine依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-turbine</artifactId>
</dependency>
```

配置文件说明：
```yaml
server:
  port: 9300

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

turbine:
  appConfig: eureka-clienta,eureka-clientd
  aggregator:
      # 指定聚合哪些集群，多个使用”,”分割，默认为default。可使用http://.../turbine.stream?cluster={clusterConfig之一}访问
      clusterConfig: default
  clusterNameExpression: new String("default")

spring:
  application:
    name: hystrix-dashboard-turbine
```

开启监控支持`@EnableTurbine`
```java
@EnableTurbine
@EnableEurekaClient
@SpringBootApplication
public class HystrixDashboardTurbineApplication {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


	public static void main(String[] args) {
		SpringApplication.run(HystrixDashboardTurbineApplication.class, args);
	}

}
```

#### 测试
启动eureka、eureka-clienta、eureka-clientc、eureka-clientd、hystrix-dashboard、turbine

1. 访问`  http://localhost:9200/hystrix`

输入监控地址：`http://localhost:9300/turbine.stream`

2. 访问`http://localhost:9001/ribbon/hello/lxy`，观察监视页面

3. 访问`http://localhost:9004/clientd/hello/lxy`，观察监视页面

4. 访问`http://localhost:9004/clientd/other/lxy`，观察监视页面

zipkin 集成 sleuth

#### 环境信息
##### 基本环境
+ spring boot 版本：1.5.19.RELEASE
+ spring cloud 版本：Edgware.SR5
+ jdk版本：1.8.0_131

##### 微服务规划
+ 注册中心：eureka
+ 微服务：eureka-clianta、eureka-clientb、eureka-clientc、zipkin-server


三个微服务，都注册在注册中心eureka

1. eureka-clienta通过ribbon的方式，访问eureka-clientc提供的微服务；
2. eureka-clientb通过feign的方式，访问eureka-clientc提供的微服务；


sleuth 是服务跟踪的，zipkin可以认为是把服务跟踪的情况可视化出来


#### zipkin-server创建

添加依赖信息

```xml
<!-- zipkin-server ui依赖 -->
<dependency>
    <groupId>io.zipkin.java</groupId>
    <artifactId>zipkin-autoconfigure-ui</artifactId>
</dependency>

<!-- zipkin-server 依赖 -->
<dependency>
    <groupId>io.zipkin.java</groupId>
    <artifactId>zipkin-server</artifactId>
</dependency>

```

开启注解支持

#### zipkin-server定义微服务
```java
@EnableEurekaClient
@EnableZipkinServer
@SpringBootApplication
public class ZipkinServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZipkinServerApplication.class, args);
	}

}
```

#### eureka-clienta 改造
添加依赖
```xml
<!-- sleuth 依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>

<!-- sleuth集成zipkin依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-zipkin</artifactId>
</dependency>
```

配置文件调整
```yaml
server:
  port: 9001

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

spring:
  application:
    name: eureka-clienta
  zipkin:
    base-url: http://localhost:9600
  sleuth:
    sampler:
#      采样比率，默认0.1，即10%
      percentage: 1.0
```
eureka-clientc类似改造

##### 测试
访问：`http://localhost:9600`

调用服务：`http://localhost:9001/ribbon/hello/lxy`

观察zipkin

![zipkin 查询.png](https://i.loli.net/2019/03/21/5c93036fa28ea.png)

查看详情

![zipkin 查看详情.png](https://i.loli.net/2019/03/21/5c93036f8ca09.png)

依赖分析

![zipkin 依赖分析.png](https://i.loli.net/2019/03/21/5c93036f993ee.png)

本工程主要用来演示配置微服务注册到eureka

#### 环境信息
##### 基本环境
+ spring boot 版本：1.5.19.RELEASE
+ spring cloud 版本：Edgware.SR5
+ jdk版本：1.8.0_131

##### 微服务规划
+ 注册中心：eureka
+ 微服务：eureka-clianta、eureka-clientb、eureka-clientc



#### eureka server 的配置

添加 eureka 依赖
```xml
<!-- eureka server 依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka-server</artifactId>
</dependency>
```

添加注解，开启eureka server支持
```java

@EnableEurekaServer
@SpringBootApplication
public class EurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaApplication.class, args);
	}

}
```

yml 配置eureka server
```yaml
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    fetch-registry: false
    register-with-eureka: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/

```

三个微服务，都注册在注册中心eureka



三个服务都添加eureka client依赖

```xml
<!-- eureka client 依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

开始eureka client 支持
```java
@EnableEurekaClient
@SpringBootApplication
public class EurekaClientaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientaApplication.class, args);
	}

}
```

yml配置文件，配置eureka server的地址
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
```
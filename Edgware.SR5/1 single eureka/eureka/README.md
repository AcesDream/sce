本项目用来演示搭建单节点的eureka server

+ spring boot 版本：1.5.19.RELEASE
+ spring cloud 版本：Edgware.SR5
+ jdk版本：1.8.0_131

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
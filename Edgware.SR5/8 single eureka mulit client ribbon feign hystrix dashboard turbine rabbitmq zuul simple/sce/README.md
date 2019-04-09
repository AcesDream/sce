zuul 配置

#### 环境信息
##### 基本环境
+ spring boot 版本：1.5.19.RELEASE
+ spring cloud 版本：Edgware.SR5
+ jdk版本：1.8.0_131

##### 微服务规划
+ 注册中心：eureka
+ 微服务：eureka-clianta、eureka-clientc、eureka-zuul


#### eureka-zuul的配置
添加依赖
```xml

<!-- zuul 依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zuul</artifactId>
</dependency>
```

配置文件调整：
```yaml
server:
  port: 9500

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

spring:
  application:
    name: eureka-zuul
```


#### 测试
启动eureka、eureka-clienta、eureka-zuul

1. 访问`  http://localhost:9500/eureka-clienta/ribbon/hello/lxy`

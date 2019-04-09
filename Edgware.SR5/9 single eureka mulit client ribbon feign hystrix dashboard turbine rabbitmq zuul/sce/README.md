zuul 配置 通过dashboard 查看 zuul的hystrix情况

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

#### dashboard 监控zuul

启动hystrix-dashboard
1. 访问`http://localhost:9200/hystrix`
2. 监控`http://localhost:9500/hystrix.stream`

#### turbine rabbitmq 监控zuul

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

配置文件调整
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
  rabbitmq:
      host: localhost
      port: 5672
      username: guest
      password: guest
```
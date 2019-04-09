### config server的高可用
#### 方案一
使用nginx负载均衡

![config server 高可用1.png](https://i.loli.net/2019/03/20/5c91f07621383.png)

#### 方案二
config server 作为eureka的一个客户端，注册到注册中心

##### congif server 改造
新增eureka client依赖
```xml
<!-- eureka 客户端依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

配置文件，注册到eureka
```yaml
server:
  port: 9801

management:
  security:
    enabled: false     #SpringBoot 1.5.X 以上默认开通了安全认证，如果不关闭会要求权限

#配置管理服务器
spring:
  application: 
    name: config-server
  profiles:
      active: native # 使用本地配置文件
  cloud:
    config:
      server:
        native:
          searchLocations: /data/api/config
    bus:
      trace:
        enabled: true # 跟踪总线事件
  rabbitmq:
      host: localhost
      port: 5672
      username: guest
      password: guest

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/   # 注册中心eurka地址
```
开启eureka client
```java
/**
 * 配置中心!
 *
 */
@EnableDiscoveryClient
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(ConfigServerApp.class, args);
    }
}

```


##### congif client 改造
新增eureka client依赖
```xml
<!-- eureka 客户端依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

配置文件调整：
```yaml
spring:
  application:
    name: config-client1                     #指定了配置文件的应用名
  cloud:
    config:
#      uri: http://localhost:9801/        #Config server的uri，直接指定地址
      label: client1                      #指定分支
      profile: dev
      name: client1                      #指定的应用的名称
      discovery:                         #通过注册中心，指定Config server
        enabled: true
        service-id: config-server
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
server:
  port: 7001
management:
  security:
    enabled: false     #SpringBoot 1.5.X 以上默认开通了安全认证，如果不关闭会要求权限

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/   # 注册中心eurka地址
```

开始服务发现功能
```java
@EnableDiscoveryClient
@SpringBootApplication
public class ConfigClient1App
{
    public static void main( String[] args )
    {
        SpringApplication.run(ConfigClient1App.class, args);
    }
}

```
##### 测试验证



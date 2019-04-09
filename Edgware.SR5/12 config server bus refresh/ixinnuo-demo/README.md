通过config server刷新应用的配置信息

#### config server的改造
新增依赖
```xml
<!-- 增加对消息总线的支持 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

调整配置文件
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
          searchLocations: /data/api/config # 配置文件的目录
  rabbitmq:
      host: localhost
      port: 5672
      username: guest
      password: guest
```

#### 测试验证
1. 修改配置文件
2. 通过post请求：`localhost:9801/bus/refresh`
3. 验证：
`http://localhost:7001/client1/hello`
`http://localhost:7002/client2/hello`
`http://localhost:7003/client3/hello`
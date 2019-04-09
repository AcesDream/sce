### 多节点自动刷新

#### 客户端调整

新增依赖

```xml
<!-- 增加对消息总线的支持 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

配置信息调整
```yaml
spring:
  application:
    name: config-client1                     #指定了配置文件的应用名
  cloud:
    config:
      uri: http://localhost:9801/        #Config server的uri
      label: client1                      #指定分支
      profile: dev
      name: client1                      #指定的应用的名称
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
server:
  port: 7001
#SpringBoot 1.5.X 以上默认开通了安全认证，如果不关闭会要求权限
management:
  security:
    enabled: false

logging:
  level: debug
```

config-client2、config-client3按照上面的方式调整

#### 测试

修改配置文件之前
![修改之前.png](https://i.loli.net/2019/03/20/5c91e959ce617.png)

修改配置文件为：
client1-dev.yml
```yaml
app:
  info: client1-hello-dev-7
```

client2-dev.yml
```yaml
app:
  info: client1-hello-dev-7
```

client3-dev.yml
```yaml
app:
  info: client1-hello-dev-7
```

刷新，通过post请求：`localhost:7001/bus/refresh`

访问以下地址进行验证，可以发现，所有应用的配置信息都更新过了：

`http://localhost:7001/client1/hello`
`http://localhost:7002/client2/hello`
`http://localhost:7003/client3/hello`

参考：`https://blog.csdn.net/wtdm_160604/article/details/83720391`

通过客户端bus/refresh

![bus refresh.png](https://i.loli.net/2019/03/20/5c91edc6a6fcc.png)

下一篇，通过config server刷新配置信息

![config server bus refresh.png](https://i.loli.net/2019/03/20/5c91edc6a9427.png)
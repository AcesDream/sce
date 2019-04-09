### spring cloud config 简单应用

#### config server
配置中心服务端依赖
```xml
<!-- spring cloud config 配置中心依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

配置信息
```yaml
server:
  port: 9801

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

```

开启config server
```java
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

配置中心客户端端
依赖信息
```xml
<!--Spring Cloud Config 客户端依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<!-- web的依赖，必须加 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

配置信息bootstrap.yml，不是application.yml
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
server:
  port: 7001
management:
  security:
    enabled: false     #SpringBoot 1.5.X 以上默认开通了安全认证，如果不关闭会要求权限

logging:
  level: debug
```

config client
```java
@SpringBootApplication
public class ConfigClient1App
{
    public static void main( String[] args )
    {
        SpringApplication.run(ConfigClient1App.class, args);
    }
}

```

测试访问：`http://localhost:7001/client1/hello`

![config client 获取配置信息.png](https://i.loli.net/2019/03/20/5c91e8342bf8d.png)


##### 配置文件自动刷新
config client新增依赖
```xml
<!--Spring Boot Actuator，感应服务端变化-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

添加注解，自动刷新支持`@RefreshScope`
```java
@RestController
@RefreshScope //开启更新功能
@RequestMapping("/client1")
public class HelloController {

	@Value("${app.info}")
	private String appInfo;

	/**
	 * 返回配置文件中的值
	 */
	@GetMapping("/hello")
	@ResponseBody
	public String getAppInfo(){
		return appInfo;
	}

}
```

测试
1. 修改配置信息app.info=client1-hello-dev-8
2. 通过postman，发起post请求`localhost:7001/refresh`
![config client 刷新.png](https://i.loli.net/2019/03/20/5c91e912e90b2.png)
3. 再次访问`http://localhost:7001/client1/hello`

![config client 刷新之后.png](https://i.loli.net/2019/03/20/5c91e959ce617.png)


存在的问题：只能一个一个的单独刷新
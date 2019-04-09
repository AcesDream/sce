### config server的加密存储
#### 对称加密
对称加密实现简单

##### 替换jce
下载地址：`https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html`

下载之后解压，然后替换`D:\tools\java\java8\jre1.8.0_131\lib\security`目录中的两个jar包


##### config server改造

配置对称加密的key，必须在bootstrap.yml中配置，在application.yml中无效

config server中新增配置文件`bootstrap.yml`
```yaml
# 对称加密的key
encrypt:
  key: Thisismysecretkey

```

+ /encrypt/status：查看加密功能状态的端点
+ /key：查看密钥的端点
+ /encrypt：对请求的body内容进行加密的端点(post)
+ /decrypt：对请求的body内容进行解密的端点)(post)


##### 加密
启动eurekaServer、ConfigServer

访问：`http://localhost:9801/encrypt/status`

![config加密状态.png](https://i.loli.net/2019/03/21/5c92edc37b762.png)

使用postman，对密码进行加密：`localhost:9801/encrypt`
![config加密.png](https://i.loli.net/2019/03/21/5c92ee3450748.png)

把加密之后的信息，存储对对应的配置文件上，单引号是必须的

client1-dev.yml

```yaml
app:
  info: client1-hello-dev-6
  password: '{cipher}031448d3a7bc4abc710a377c577110c8dcce67944ab1093cd2bf496366d8d97a'
```

##### 调整config-client1
```java

@RestController
@RefreshScope //开启更新功能
@RequestMapping("/client1")
public class HelloController {

	@Value("${app.info}")
	private String appInfo;

	@Value("${app.password}")
	private String password;

	/**
	 * 返回配置文件中的值
	 */
	@GetMapping("/hello")
	@ResponseBody
	public String getAppInfo(){
		return appInfo + password;
	}

}

```

启动config-client1，访问：`http://localhost:7001/client1/hello`

说明：配置文件中已加密

![已加密配置文件.png](https://i.loli.net/2019/03/21/5c92efad5d52d.png)

客户端使用的时候已解密

![config server端已加密.png](https://i.loli.net/2019/03/21/5c92ef6321922.png)


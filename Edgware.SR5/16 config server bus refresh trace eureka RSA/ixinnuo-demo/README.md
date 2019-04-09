### config server的加密存储
#### 非对称加密
非对称加密实现简单

前提已替换jce
##### config server改造

生成证书：
`
keytool -genkeypair -alias config-server -keyalg RSA -dname "CN=ms-config-server, OU=company, O=organization, L=city, ST=province, C=china" -keypass 222222 -keystore config-server.keystore -storepass 111111 -validity 365 
`

把config-server.keystore放在resources目录下


调整配置文件
config server中新增配置文件`bootstrap.yml`
```yaml
#设置再application.yml会提示No key was installed for encryption service

#1. 配置JCE
#2. bootstrap.yml中设置key，不能在application.yml中设置
# 可通过以下端点（/encrypt/status）来访问查看

# 对称加密的key
#encrypt:
#  key: Thisismysecretkey

# 非对称加密
encrypt:
  key-store:
    location: classpath:/config-server.keystore
    password: 111111
    alias: config-server
    secret: 222222

```

##### 加密
启动eurekaServer、ConfigServer

使用postman，对密码进行加密：`localhost:9801/encrypt`

`qwe123`加密之后的密文

`AQA9g12n2erx0zL1jp7B6p9qknzYIXaG/KtszdCPRABct2UW7xzL8tGjLfow2xxkJKvysLMtd2r02dILQbHRBS/LSu+xJbSYNLJwjdkKWVBf6RTzPYQ8FaqHMBComCATafUryNZee5s1DvDJdAyYAvkOfkt+kb+Apw050cENs6QoCSDa0pCl2JJ+YNrGFnZFXytP27OUGzKTll9lJanUiq7n8RK/MGH+UbyzSzXUh//qNBi6esA3JG5hMsvmtq++bj3EnBI99qCDWtoNE4YQ3P/jM4y75b9O+RnqgMDakR6SH85347FQ7QeyFTQZRKWWPmYLcxnahnrXrwNA+ZufJ89QjVfxj6anjJcpvsQbTx2hqESGEmlnfuN6lA0/Xtx2Vfs=`

![非对称加密之后.png](https://i.loli.net/2019/03/21/5c92f28e48283.png)


把加密之后的信息，存储对对应的配置文件上，单引号是必须的

client1-dev.yml

```yaml
app:
  info: client1-hello-dev-6
  #对称加密的密文
  #password: '{cipher}031448d3a7bc4abc710a377c577110c8dcce67944ab1093cd2bf496366d8d97a'
  #非对称加密的密文
  password: '{cipher}AQA9g12n2erx0zL1jp7B6p9qknzYIXaG/KtszdCPRABct2UW7xzL8tGjLfow2xxkJKvysLMtd2r02dILQbHRBS/LSu+xJbSYNLJwjdkKWVBf6RTzPYQ8FaqHMBComCATafUryNZee5s1DvDJdAyYAvkOfkt+kb+Apw050cENs6QoCSDa0pCl2JJ+YNrGFnZFXytP27OUGzKTll9lJanUiq7n8RK/MGH+UbyzSzXUh//qNBi6esA3JG5hMsvmtq++bj3EnBI99qCDWtoNE4YQ3P/jM4y75b9O+RnqgMDakR6SH85347FQ7QeyFTQZRKWWPmYLcxnahnrXrwNA+ZufJ89QjVfxj6anjJcpvsQbTx2hqESGEmlnfuN6lA0/Xtx2Vfs='
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

![非对称加密配置文件.png](https://i.loli.net/2019/03/21/5c92f3cea0693.png)

客户端使用的时候已解密

![config server端已加密.png](https://i.loli.net/2019/03/21/5c92ef6321922.png)


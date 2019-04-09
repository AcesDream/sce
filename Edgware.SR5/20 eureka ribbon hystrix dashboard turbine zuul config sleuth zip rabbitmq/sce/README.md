本工程使用turbine 聚合 hytrix监控

#### 环境信息
##### 基本环境
+ spring boot 版本：1.5.19.RELEASE
+ spring cloud 版本：Edgware.SR5
+ jdk版本：1.8.0_131

##### 微服务规划
+ 注册中心：eureka
+ 微服务：eureka-clianta、eureka-clientc、eureka-clientd、hystrix-dashboard、turbine-rabbitmq、eureka-zuul、zipkin-server、config-server


所有微服务，都注册在注册中心eureka

1. eureka-clienta通过ribbon的方式eureka-clientd， eureka-clientd通过ribbon的方式，访问eureka-clientc提供的微服务；
2. turbine-rabbitmq聚合多个hytrix的信息，
3. hystrix-dashboard可以监控turbine，实现监控多个微服务的调用情况
4. eureka-clienta、eureka-clientc、eureka-clientd、eureka-zuul通过config-server获取启动信息，集成了bus；
5、eureka-clienta、eureka-clientc、eureka-clientd、eureka-zuul通过http的方式与zipkin-server集成
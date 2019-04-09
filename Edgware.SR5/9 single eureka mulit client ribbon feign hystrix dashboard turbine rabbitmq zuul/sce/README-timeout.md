zuul的超时情况研究


#### 环境信息
##### 基本环境
+ spring boot 版本：1.5.19.RELEASE
+ spring cloud 版本：Edgware.SR5
+ jdk版本：1.8.0_131

##### 微服务规划
+ 注册中心：eureka
+ 微服务：eureka-clianta、eureka-clientc、eureka-zuul


#### zuul的超时时间
zuul的超时一共有三个部分：zuul本身的超时、ribbon的超时、hystrix的超时；


```yaml
zuul:
  max:
    host:
      connections: 500
  host:
    #默认1000
    socket-timeout-millis: 3000
    #默认2000
    connect-timeout-millis: 3000
  routes:
#  指定微服务的Id，直接指定访问url
    eureka-clienta: /eca/**
#   clienta是自定义名称，下面指定service-id和path来配置路由，使用service-id的方式，超时时间ribbon生效
    clienta:
      service-id: eureka-clienta
      path: /oeca/**
#   clientb是自定义名称，下面指定url和path来配置路由，使用url的方式，超时时间是zuul.host生效
    clientb:
      path: /oecb/**
      url: http://localhost:9001/
#      忽略某一个微服务
#  ignored-services: eureka-clientc
#  忽略所有的微服务，打开了这个，则路由上面指定的配置
#  ignored-services: '*'


ribbon:
  #对所有操作请求都进行重试,默认false
  OkToRetryOnAllOperations: false
  #负载均衡超时时间，默认值5000
  ReadTimeout: 20
  #ribbon请求连接的超时时间，默认值2000
  ConnectTimeout: 1000
  #对当前实例的重试次数，默认0
  MaxAutoRetries: 0
  #对切换实例的重试次数，默认1
  MaxAutoRetriesNextServer: 1


# 所以hystrix的超时时间要大于 (1 + MaxAutoRetries + MaxAutoRetriesNextServer) * (ReadTimeout+ConnectTimeout) 比较好，具体看需求进行配置。
hystrix:
  command:
    #default全局有效，service id指定应用有效
    default:
      execution:
        timeout:
          #如果enabled设置为false，则请求超时交给ribbon控制,为true,则超时作为熔断根据
          enabled: true
        isolation:
          thread:
            #断路器超时时间，默认1000ms
            timeoutInMilliseconds: 2000

```

#### 超时测试
##### service-id方式
这种方式，起作用的是ribbon和hystrix，哪个小，哪个起作用，会触发zuul的fallback

测试1，ribbon的超时时间小，
```yaml
ribbon:
  #对所有操作请求都进行重试,默认false
  OkToRetryOnAllOperations: false
  #负载均衡超时时间，默认值5000
  ReadTimeout: 20
  #ribbon请求连接的超时时间，默认值2000
  ConnectTimeout: 1000
  #对当前实例的重试次数，默认0
  MaxAutoRetries: 0
  #对切换实例的重试次数，默认1
  MaxAutoRetriesNextServer: 1


# 所以hystrix的超时时间要大于 (1 + MaxAutoRetries + MaxAutoRetriesNextServer) * (ReadTimeout+ConnectTimeout) 比较好，具体看需求进行配置。
hystrix:
  command:
    #default全局有效，service id指定应用有效
    default:
      execution:
        timeout:
          #如果enabled设置为false，则请求超时交给ribbon控制,为true,则超时作为熔断根据
          enabled: true
        isolation:
          thread:
            #断路器超时时间，默认1000ms
            timeoutInMilliseconds: 2000
```

访问：`http://localhost:9500/oeca/ribbon/hello/lxy`

观察控制台输出，可见是ribbon超时起作用
```text

com.netflix.client.ClientException: null
	at com.netflix.client.AbstractLoadBalancerAwareClient.executeWithLoadBalancer(AbstractLoadBalancerAwareClient.java:118) ~[ribbon-loadbalancer-2.2.5.jar:2.2.5]
	at org.springframework.cloud.netflix.zuul.filters.route.support.AbstractRibbonCommand.run(AbstractRibbonCommand.java:187) ~[spring-cloud-netflix-core-1.4.6.RELEASE.jar:1.4.6.RELEASE]
	at org.springframework.cloud.netflix.zuul.filters.route.support.AbstractRibbonCommand.run(AbstractRibbonCommand.java:52) ~[spring-cloud-netflix-core-1.4.6.RELEASE.jar:1.4.6.RELEASE]
	at com.netflix.hystrix.HystrixCommand$2.call(HystrixCommand.java:302) ~[hystrix-core-1.5.12.jar:1.5.12]
	at com.netflix.hystrix.HystrixCommand$2.call(HystrixCommand.java:298) ~[hystrix-core-1.5.12.jar:1.5.12]
	at rx.internal.operators.OnSubscribeDefer.call(OnSubscribeDefer.java:46) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDefer.call(OnSubscribeDefer.java:35) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:48) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:48) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:48) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.unsafeSubscribe(Observable.java:10151) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDefer.call(OnSubscribeDefer.java:51) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDefer.call(OnSubscribeDefer.java:35) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:48) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.unsafeSubscribe(Observable.java:10151) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:41) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.unsafeSubscribe(Observable.java:10151) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:41) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:48) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.unsafeSubscribe(Observable.java:10151) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:41) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.unsafeSubscribe(Observable.java:10151) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:41) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.unsafeSubscribe(Observable.java:10151) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:41) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:48) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.unsafeSubscribe(Observable.java:10151) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDefer.call(OnSubscribeDefer.java:51) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDefer.call(OnSubscribeDefer.java:35) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.unsafeSubscribe(Observable.java:10151) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeMap.call(OnSubscribeMap.java:48) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeMap.call(OnSubscribeMap.java:33) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.unsafeSubscribe(Observable.java:10151) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:41) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:48) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.unsafeSubscribe(Observable.java:10151) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:41) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDoOnEach.call(OnSubscribeDoOnEach.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.unsafeSubscribe(Observable.java:10151) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDefer.call(OnSubscribeDefer.java:51) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeDefer.call(OnSubscribeDefer.java:35) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:48) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.subscribe(Observable.java:10247) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.subscribe(Observable.java:10214) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.BlockingOperatorToFuture.toFuture(BlockingOperatorToFuture.java:51) [rxjava-1.2.0.jar:1.2.0]
	at rx.observables.BlockingObservable.toFuture(BlockingObservable.java:411) [rxjava-1.2.0.jar:1.2.0]
	at com.netflix.hystrix.HystrixCommand.queue(HystrixCommand.java:378) [hystrix-core-1.5.12.jar:1.5.12]
	at com.netflix.hystrix.HystrixCommand.execute(HystrixCommand.java:344) [hystrix-core-1.5.12.jar:1.5.12]
	at org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter.forward(RibbonRoutingFilter.java:158) [spring-cloud-netflix-core-1.4.6.RELEASE.jar:1.4.6.RELEASE]
	at org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter.run(RibbonRoutingFilter.java:111) [spring-cloud-netflix-core-1.4.6.RELEASE.jar:1.4.6.RELEASE]
	at com.netflix.zuul.ZuulFilter.runFilter(ZuulFilter.java:117) [zuul-core-1.3.1.jar:1.3.1]
	at com.netflix.zuul.FilterProcessor.processZuulFilter(FilterProcessor.java:193) [zuul-core-1.3.1.jar:1.3.1]
	at com.netflix.zuul.FilterProcessor.runFilters(FilterProcessor.java:157) [zuul-core-1.3.1.jar:1.3.1]
	at com.netflix.zuul.FilterProcessor.route(FilterProcessor.java:118) [zuul-core-1.3.1.jar:1.3.1]
	at com.netflix.zuul.ZuulRunner.route(ZuulRunner.java:96) [zuul-core-1.3.1.jar:1.3.1]
	at com.netflix.zuul.http.ZuulServlet.route(ZuulServlet.java:116) [zuul-core-1.3.1.jar:1.3.1]
	at com.netflix.zuul.http.ZuulServlet.service(ZuulServlet.java:81) [zuul-core-1.3.1.jar:1.3.1]
	at org.springframework.web.servlet.mvc.ServletWrappingController.handleRequestInternal(ServletWrappingController.java:157) [spring-webmvc-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.springframework.cloud.netflix.zuul.web.ZuulController.handleRequest(ZuulController.java:44) [spring-cloud-netflix-core-1.4.6.RELEASE.jar:1.4.6.RELEASE]
	at org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter.handle(SimpleControllerHandlerAdapter.java:50) [spring-webmvc-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:967) [spring-webmvc-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:901) [spring-webmvc-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:970) [spring-webmvc-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:861) [spring-webmvc-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:635) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:846) [spring-webmvc-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:742) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:52) [tomcat-embed-websocket-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.springframework.boot.web.filter.ApplicationContextHeaderFilter.doFilterInternal(ApplicationContextHeaderFilter.java:55) [spring-boot-1.5.19.RELEASE.jar:1.5.19.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107) [spring-web-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.springframework.boot.actuate.trace.WebRequestTraceFilter.doFilterInternal(WebRequestTraceFilter.java:111) [spring-boot-actuator-1.5.19.RELEASE.jar:1.5.19.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107) [spring-web-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:99) [spring-web-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107) [spring-web-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.springframework.web.filter.HttpPutFormContentFilter.doFilterInternal(HttpPutFormContentFilter.java:109) [spring-web-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107) [spring-web-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.springframework.web.filter.HiddenHttpMethodFilter.doFilterInternal(HiddenHttpMethodFilter.java:93) [spring-web-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107) [spring-web-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:197) [spring-web-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107) [spring-web-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.springframework.boot.actuate.autoconfigure.MetricsFilter.doFilterInternal(MetricsFilter.java:103) [spring-boot-actuator-1.5.19.RELEASE.jar:1.5.19.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107) [spring-web-4.3.22.RELEASE.jar:4.3.22.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:198) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:493) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:140) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:81) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:87) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:342) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:800) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:66) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:806) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1498) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142) [na:1.8.0_131]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617) [na:1.8.0_131]
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) [tomcat-embed-core-8.5.37.jar:8.5.37]
	at java.lang.Thread.run(Thread.java:748) [na:1.8.0_131]
Caused by: java.lang.RuntimeException: java.net.SocketTimeoutException: Read timed out
	at rx.exceptions.Exceptions.propagate(Exceptions.java:58) ~[rxjava-1.2.0.jar:1.2.0]
	at rx.observables.BlockingObservable.blockForSingle(BlockingObservable.java:464) [rxjava-1.2.0.jar:1.2.0]
	at rx.observables.BlockingObservable.single(BlockingObservable.java:341) [rxjava-1.2.0.jar:1.2.0]
	at com.netflix.client.AbstractLoadBalancerAwareClient.executeWithLoadBalancer(AbstractLoadBalancerAwareClient.java:112) ~[ribbon-loadbalancer-2.2.5.jar:2.2.5]
	... 129 common frames omitted
Caused by: java.net.SocketTimeoutException: Read timed out
	at java.net.SocketInputStream.socketRead0(Native Method) ~[na:1.8.0_131]
	at java.net.SocketInputStream.socketRead(SocketInputStream.java:116) ~[na:1.8.0_131]
	at java.net.SocketInputStream.read(SocketInputStream.java:171) ~[na:1.8.0_131]
	at java.net.SocketInputStream.read(SocketInputStream.java:141) ~[na:1.8.0_131]
	at org.apache.http.impl.io.SessionInputBufferImpl.streamRead(SessionInputBufferImpl.java:137) ~[httpcore-4.4.10.jar:4.4.10]
	at org.apache.http.impl.io.SessionInputBufferImpl.fillBuffer(SessionInputBufferImpl.java:153) ~[httpcore-4.4.10.jar:4.4.10]
	at org.apache.http.impl.io.SessionInputBufferImpl.readLine(SessionInputBufferImpl.java:282) ~[httpcore-4.4.10.jar:4.4.10]
	at org.apache.http.impl.conn.DefaultHttpResponseParser.parseHead(DefaultHttpResponseParser.java:138) ~[httpclient-4.5.6.jar:4.5.6]
	at org.apache.http.impl.conn.DefaultHttpResponseParser.parseHead(DefaultHttpResponseParser.java:56) ~[httpclient-4.5.6.jar:4.5.6]
	at org.apache.http.impl.io.AbstractMessageParser.parse(AbstractMessageParser.java:259) ~[httpcore-4.4.10.jar:4.4.10]
	at org.apache.http.impl.DefaultBHttpClientConnection.receiveResponseHeader(DefaultBHttpClientConnection.java:163) ~[httpcore-4.4.10.jar:4.4.10]
	at org.apache.http.impl.conn.CPoolProxy.receiveResponseHeader(CPoolProxy.java:165) ~[httpclient-4.5.6.jar:4.5.6]
	at org.apache.http.protocol.HttpRequestExecutor.doReceiveResponse(HttpRequestExecutor.java:273) ~[httpcore-4.4.10.jar:4.4.10]
	at org.apache.http.protocol.HttpRequestExecutor.execute(HttpRequestExecutor.java:125) ~[httpcore-4.4.10.jar:4.4.10]
	at org.apache.http.impl.execchain.MainClientExec.execute(MainClientExec.java:272) ~[httpclient-4.5.6.jar:4.5.6]
	at org.apache.http.impl.execchain.ProtocolExec.execute(ProtocolExec.java:185) ~[httpclient-4.5.6.jar:4.5.6]
	at org.apache.http.impl.execchain.RetryExec.execute(RetryExec.java:89) ~[httpclient-4.5.6.jar:4.5.6]
	at org.apache.http.impl.execchain.RedirectExec.execute(RedirectExec.java:110) ~[httpclient-4.5.6.jar:4.5.6]
	at org.apache.http.impl.client.InternalHttpClient.doExecute(InternalHttpClient.java:185) ~[httpclient-4.5.6.jar:4.5.6]
	at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:83) ~[httpclient-4.5.6.jar:4.5.6]
	at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:108) ~[httpclient-4.5.6.jar:4.5.6]
	at org.springframework.cloud.netflix.ribbon.apache.RetryableRibbonLoadBalancingHttpClient$1.doWithRetry(RetryableRibbonLoadBalancingHttpClient.java:149) ~[spring-cloud-netflix-core-1.4.6.RELEASE.jar:1.4.6.RELEASE]
	at org.springframework.cloud.netflix.ribbon.apache.RetryableRibbonLoadBalancingHttpClient$1.doWithRetry(RetryableRibbonLoadBalancingHttpClient.java:124) ~[spring-cloud-netflix-core-1.4.6.RELEASE.jar:1.4.6.RELEASE]
	at org.springframework.retry.support.RetryTemplate.doExecute(RetryTemplate.java:287) ~[spring-retry-1.2.3.RELEASE.jar:na]
	at org.springframework.retry.support.RetryTemplate.execute(RetryTemplate.java:180) ~[spring-retry-1.2.3.RELEASE.jar:na]
	at org.springframework.cloud.netflix.ribbon.apache.RetryableRibbonLoadBalancingHttpClient.executeWithRetry(RetryableRibbonLoadBalancingHttpClient.java:192) ~[spring-cloud-netflix-core-1.4.6.RELEASE.jar:1.4.6.RELEASE]
	at org.springframework.cloud.netflix.ribbon.apache.RetryableRibbonLoadBalancingHttpClient.execute(RetryableRibbonLoadBalancingHttpClient.java:166) ~[spring-cloud-netflix-core-1.4.6.RELEASE.jar:1.4.6.RELEASE]
	at org.springframework.cloud.netflix.ribbon.apache.RetryableRibbonLoadBalancingHttpClient.execute(RetryableRibbonLoadBalancingHttpClient.java:61) ~[spring-cloud-netflix-core-1.4.6.RELEASE.jar:1.4.6.RELEASE]
	at com.netflix.client.AbstractLoadBalancerAwareClient$1.call(AbstractLoadBalancerAwareClient.java:104) ~[ribbon-loadbalancer-2.2.5.jar:2.2.5]
	at com.netflix.loadbalancer.reactive.LoadBalancerCommand$3$1.call(LoadBalancerCommand.java:303) ~[ribbon-loadbalancer-2.2.5.jar:2.2.5]
	at com.netflix.loadbalancer.reactive.LoadBalancerCommand$3$1.call(LoadBalancerCommand.java:287) ~[ribbon-loadbalancer-2.2.5.jar:2.2.5]
	at rx.internal.util.ScalarSynchronousObservable$3.call(ScalarSynchronousObservable.java:231) ~[rxjava-1.2.0.jar:1.2.0]
	at rx.internal.util.ScalarSynchronousObservable$3.call(ScalarSynchronousObservable.java:228) ~[rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.unsafeSubscribe(Observable.java:10151) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeConcatMap$ConcatMapSubscriber.drain(OnSubscribeConcatMap.java:286) ~[rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeConcatMap$ConcatMapSubscriber.onNext(OnSubscribeConcatMap.java:144) ~[rxjava-1.2.0.jar:1.2.0]
	at com.netflix.loadbalancer.reactive.LoadBalancerCommand$1.call(LoadBalancerCommand.java:185) ~[ribbon-loadbalancer-2.2.5.jar:2.2.5]
	at com.netflix.loadbalancer.reactive.LoadBalancerCommand$1.call(LoadBalancerCommand.java:180) ~[ribbon-loadbalancer-2.2.5.jar:2.2.5]
	at rx.Observable.unsafeSubscribe(Observable.java:10151) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeConcatMap.call(OnSubscribeConcatMap.java:94) ~[rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeConcatMap.call(OnSubscribeConcatMap.java:42) ~[rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:48) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:48) [rxjava-1.2.0.jar:1.2.0]
	at rx.internal.operators.OnSubscribeLift.call(OnSubscribeLift.java:30) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.subscribe(Observable.java:10247) [rxjava-1.2.0.jar:1.2.0]
	at rx.Observable.subscribe(Observable.java:10214) [rxjava-1.2.0.jar:1.2.0]
	at rx.observables.BlockingObservable.blockForSingle(BlockingObservable.java:444) [rxjava-1.2.0.jar:1.2.0]
	... 131 common frames omitted

```

测试2，hystrix的超时时间小
```yaml
ribbon:
  #对所有操作请求都进行重试,默认false
  OkToRetryOnAllOperations: false
  #负载均衡超时时间，默认值5000
  ReadTimeout: 2000
  #ribbon请求连接的超时时间，默认值2000
  ConnectTimeout: 1000
  #对当前实例的重试次数，默认0
  MaxAutoRetries: 0
  #对切换实例的重试次数，默认1
  MaxAutoRetriesNextServer: 1


# 所以hystrix的超时时间要大于 (1 + MaxAutoRetries + MaxAutoRetriesNextServer) * (ReadTimeout+ConnectTimeout) 比较好，具体看需求进行配置。
hystrix:
  command:
    #default全局有效，service id指定应用有效
    default:
      execution:
        timeout:
          #如果enabled设置为false，则请求超时交给ribbon控制,为true,则超时作为熔断根据
          enabled: true
        isolation:
          thread:
            #断路器超时时间，默认1000ms
            timeoutInMilliseconds: 1
```

访问：`http://localhost:9500/oeca/ribbon/hello/lxy`

观察控制台输出，可见是hystrix起作用，触发fallback：
```text
2019-03-20 13:40:56.184  WARN 17544 --- [nio-9500-exec-2] o.s.c.n.z.f.r.s.AbstractRibbonCommand    : The Hystrix timeout of 1ms for the command eureka-clienta is set lower than the combination of the Ribbon read and connect timeout, 6000ms.
2019-03-20 13:40:56.188 ERROR 17544 --- [ HystrixTimer-1] c.s.s.e.fallback.EurekaClientaFallback   : 异常：{}

com.netflix.hystrix.exception.HystrixTimeoutException: null
	at com.netflix.hystrix.AbstractCommand$HystrixObservableTimeoutOperator$1$1.run(AbstractCommand.java:1154) [hystrix-core-1.5.12.jar:1.5.12]
	at com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable$1.call(HystrixContextRunnable.java:45) [hystrix-core-1.5.12.jar:1.5.12]
	at com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable$1.call(HystrixContextRunnable.java:41) [hystrix-core-1.5.12.jar:1.5.12]
	at com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable.run(HystrixContextRunnable.java:61) [hystrix-core-1.5.12.jar:1.5.12]
	at com.netflix.hystrix.AbstractCommand$HystrixObservableTimeoutOperator$1.tick(AbstractCommand.java:1159) [hystrix-core-1.5.12.jar:1.5.12]
	at com.netflix.hystrix.util.HystrixTimer$1.run(HystrixTimer.java:99) [hystrix-core-1.5.12.jar:1.5.12]
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) [na:1.8.0_131]
	at java.util.concurrent.FutureTask.runAndReset(FutureTask.java:308) [na:1.8.0_131]
	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$301(ScheduledThreadPoolExecutor.java:180) [na:1.8.0_131]
	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:294) [na:1.8.0_131]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142) [na:1.8.0_131]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617) [na:1.8.0_131]
	at java.lang.Thread.run(Thread.java:748) [na:1.8.0_131]


```


测试3，eureka-clienta中fallback触发
```yaml
ribbon:
  #对所有操作请求都进行重试,默认false
  OkToRetryOnAllOperations: false
  #负载均衡超时时间，默认值5000
  ReadTimeout: 2000
  #ribbon请求连接的超时时间，默认值2000
  ConnectTimeout: 1000
  #对当前实例的重试次数，默认0
  MaxAutoRetries: 0
  #对切换实例的重试次数，默认1
  MaxAutoRetriesNextServer: 1


# 所以hystrix的超时时间要大于 (1 + MaxAutoRetries + MaxAutoRetriesNextServer) * (ReadTimeout+ConnectTimeout) 比较好，具体看需求进行配置。
hystrix:
  command:
    #default全局有效，service id指定应用有效
    default:
      execution:
        timeout:
          #如果enabled设置为false，则请求超时交给ribbon控制,为true,则超时作为熔断根据
          enabled: true
        isolation:
          thread:
            #断路器超时时间，默认1000ms
            timeoutInMilliseconds: 6000
```

访问：`http://localhost:9500/oeca/ribbon/hello/lxy`

eureka-clienta的fallback被触发，因为zuul并没有超时，但是eureka-clienta中的getHelloMessage超时了，因此触发了fallback

##### url方式访问

测试1
zuul.host.socket-timeout-millis正常设置，ribbon和hystrix的正常设置

```yaml
zuul:
  max:
    host:
      connections: 500
  host:
    #默认1000
    socket-timeout-millis: 2000
    #默认2000
    connect-timeout-millis: 1000
  routes:
#  指定微服务的Id，直接指定访问url
    eureka-clienta: /eca/**
#   clienta是自定义名称，下面指定service-id和path来配置路由，使用service-id的方式，超时时间ribbon生效
    clienta:
      service-id: eureka-clienta
      path: /oeca/**
#   clientb是自定义名称，下面指定url和path来配置路由，使用url的方式，超时时间是zuul.host生效
    clientb:
      path: /oecb/**
      url: http://localhost:9001/
#      忽略某一个微服务
#  ignored-services: eureka-clientc
#  忽略所有的微服务，打开了这个，则路由上面指定的配置
#  ignored-services: '*'


ribbon:
  #对所有操作请求都进行重试,默认false
  OkToRetryOnAllOperations: false
  #负载均衡超时时间，默认值5000
  ReadTimeout: 2000
  #ribbon请求连接的超时时间，默认值2000
  ConnectTimeout: 1000
  #对当前实例的重试次数，默认0
  MaxAutoRetries: 0
  #对切换实例的重试次数，默认1
  MaxAutoRetriesNextServer: 1


# 所以hystrix的超时时间要大于 (1 + MaxAutoRetries + MaxAutoRetriesNextServer) * (ReadTimeout+ConnectTimeout) 比较好，具体看需求进行配置。
hystrix:
  command:
    #default全局有效，service id指定应用有效
    default:
      execution:
        timeout:
          #如果enabled设置为false，则请求超时交给ribbon控制,为true,则超时作为熔断根据
          enabled: true
        isolation:
          thread:
            #断路器超时时间，默认1000ms
            timeoutInMilliseconds: 6000


```

访问：`http://localhost:9500/oecb/ribbon/hello/lxy`
触发eureka-clienta的fallback

测试2：调整hystrix的时间为1

```yaml
zuul:
  max:
    host:
      connections: 500
  host:
    #默认1000
    socket-timeout-millis: 2000
    #默认2000
    connect-timeout-millis: 1000
  routes:
#  指定微服务的Id，直接指定访问url
    eureka-clienta: /eca/**
#   clienta是自定义名称，下面指定service-id和path来配置路由，使用service-id的方式，超时时间ribbon生效
    clienta:
      service-id: eureka-clienta
      path: /oeca/**
#   clientb是自定义名称，下面指定url和path来配置路由，使用url的方式，超时时间是zuul.host生效
    clientb:
      path: /oecb/**
      url: http://localhost:9001/
#      忽略某一个微服务
#  ignored-services: eureka-clientc
#  忽略所有的微服务，打开了这个，则路由上面指定的配置
#  ignored-services: '*'


ribbon:
  #对所有操作请求都进行重试,默认false
  OkToRetryOnAllOperations: false
  #负载均衡超时时间，默认值5000
  ReadTimeout: 2000
  #ribbon请求连接的超时时间，默认值2000
  ConnectTimeout: 1000
  #对当前实例的重试次数，默认0
  MaxAutoRetries: 0
  #对切换实例的重试次数，默认1
  MaxAutoRetriesNextServer: 1


# 所以hystrix的超时时间要大于 (1 + MaxAutoRetries + MaxAutoRetriesNextServer) * (ReadTimeout+ConnectTimeout) 比较好，具体看需求进行配置。
hystrix:
  command:
    #default全局有效，service id指定应用有效
    default:
      execution:
        timeout:
          #如果enabled设置为false，则请求超时交给ribbon控制,为true,则超时作为熔断根据
          enabled: true
        isolation:
          thread:
            #断路器超时时间，默认1000ms
            timeoutInMilliseconds: 1


```

访问：`http://localhost:9500/oecb/ribbon/hello/lxy`

触发eureka-clienta的fallback，说明url的方式访问，hystrix超时不起作用



测试2：调整ribbon的时间为1

```yaml
zuul:
  max:
    host:
      connections: 500
  host:
    #默认1000
    socket-timeout-millis: 2000
    #默认2000
    connect-timeout-millis: 1000
  routes:
#  指定微服务的Id，直接指定访问url
    eureka-clienta: /eca/**
#   clienta是自定义名称，下面指定service-id和path来配置路由，使用service-id的方式，超时时间ribbon生效
    clienta:
      service-id: eureka-clienta
      path: /oeca/**
#   clientb是自定义名称，下面指定url和path来配置路由，使用url的方式，超时时间是zuul.host生效
    clientb:
      path: /oecb/**
      url: http://localhost:9001/
#      忽略某一个微服务
#  ignored-services: eureka-clientc
#  忽略所有的微服务，打开了这个，则路由上面指定的配置
#  ignored-services: '*'


ribbon:
  #对所有操作请求都进行重试,默认false
  OkToRetryOnAllOperations: false
  #负载均衡超时时间，默认值5000
  ReadTimeout: 1
  #ribbon请求连接的超时时间，默认值2000
  ConnectTimeout: 1
  #对当前实例的重试次数，默认0
  MaxAutoRetries: 0
  #对切换实例的重试次数，默认1
  MaxAutoRetriesNextServer: 1


# 所以hystrix的超时时间要大于 (1 + MaxAutoRetries + MaxAutoRetriesNextServer) * (ReadTimeout+ConnectTimeout) 比较好，具体看需求进行配置。
hystrix:
  command:
    #default全局有效，service id指定应用有效
    default:
      execution:
        timeout:
          #如果enabled设置为false，则请求超时交给ribbon控制,为true,则超时作为熔断根据
          enabled: true
        isolation:
          thread:
            #断路器超时时间，默认1000ms
            timeoutInMilliseconds: 2000


```

访问：`http://localhost:9500/oecb/ribbon/hello/lxy`

触发eureka-clienta的fallback，说明url的方式访问，ribbon超时不起作用


测试3：调整zuul的超时时间


```yaml
zuul:
  max:
    host:
      connections: 500
  host:
    #默认1000
    socket-timeout-millis: 1
    #默认2000
    connect-timeout-millis: 1
  routes:
#  指定微服务的Id，直接指定访问url
    eureka-clienta: /eca/**
#   clienta是自定义名称，下面指定service-id和path来配置路由，使用service-id的方式，超时时间ribbon生效
    clienta:
      service-id: eureka-clienta
      path: /oeca/**
#   clientb是自定义名称，下面指定url和path来配置路由，使用url的方式，超时时间是zuul.host生效
    clientb:
      path: /oecb/**
      url: http://localhost:9001/
#      忽略某一个微服务
#  ignored-services: eureka-clientc
#  忽略所有的微服务，打开了这个，则路由上面指定的配置
#  ignored-services: '*'


ribbon:
  #对所有操作请求都进行重试,默认false
  OkToRetryOnAllOperations: false
  #负载均衡超时时间，默认值5000
  ReadTimeout: 2000
  #ribbon请求连接的超时时间，默认值2000
  ConnectTimeout: 1000
  #对当前实例的重试次数，默认0
  MaxAutoRetries: 0
  #对切换实例的重试次数，默认1
  MaxAutoRetriesNextServer: 1


# 所以hystrix的超时时间要大于 (1 + MaxAutoRetries + MaxAutoRetriesNextServer) * (ReadTimeout+ConnectTimeout) 比较好，具体看需求进行配置。
hystrix:
  command:
    #default全局有效，service id指定应用有效
    default:
      execution:
        timeout:
          #如果enabled设置为false，则请求超时交给ribbon控制,为true,则超时作为熔断根据
          enabled: true
        isolation:
          thread:
            #断路器超时时间，默认1000ms
            timeoutInMilliseconds: 2000


```


访问：`http://localhost:9500/oecb/ribbon/hello/lxy`

说明url的方式访问，说明zuul的超时起作用

![TIM截图20190320135455.png](https://i.loli.net/2019/03/20/5c91d5d1153f0.png)



##### 添加error过滤器
zuul一共提供了四种默认的过滤器：pre、route、post、error

+ PRE: 该类型的filters在Request routing到源web-service之前执行。用来实现Authentication、选择源服务地址等
+ ROUTING：该类型的filters用于把Request routing到源web-service，源web-service是实现业务逻辑的服务。这里使用HttpClient请求web-service。
+ POST：该类型的filters在ROUTING返回Response后执行。用来实现对Response结果进行修改，收集统计数据以及把Response传输会客户端。
+ ERROR：上面三个过程中任何一个出现错误都交由ERROR类型的filters进行处理。

主要关注 pre、post和error。分别代表前置过滤，后置过滤和异常过滤。

如果你的filter是pre的，像上一篇那种，就是指请求先进入pre的filter类，你可以进行一些权限认证，日志记录，或者额外给Request增加一些属性供后续的filter使用。pre会优先按照order从小到大执行，然后再去执行请求转发到业务服务。

再说post，如果type为post，那么就会执行完被路由的业务服务后，再进入post的filter，在post的filter里，一般做一些日志记录，或者额外增加response属性什么的。

最后error，如果在上面的任何一个地方出现了异常，就会进入到type为error的filter中。


![TIM截图20190320142929.png](https://i.loli.net/2019/03/20/5c91dddd787f5.png)


处理500返回结果的问题，定义ErrorHandler
```java
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorHandlerController implements ErrorController {

	/**
	 * 出异常后进入该方法，交由下面的方法处理
	 */
	@Override
	public String getErrorPath() {
		return "/error";
	}

	@RequestMapping("/error")
	public String error() {
		return "出现异常";
	}
}

```


参考文章：
+ zuul网关Filter处理流程及异常处理：`https://blog.csdn.net/tianyaleixiaowu/article/details/77893822`
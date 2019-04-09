微服务跟踪

#### 环境信息
##### 基本环境
+ spring boot 版本：1.5.19.RELEASE
+ spring cloud 版本：Edgware.SR5
+ jdk版本：1.8.0_131

##### 微服务规划
+ 注册中心：eureka
+ 微服务：eureka-clianta、eureka-clientc



#### eureka clienta 的配置

添加 sleuth 依赖
```xml
<!-- sleuth 依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```

配置日志级别
```yaml
server:
  port: 9001

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

spring:
  application:
    name: eureka-clienta
logging:
  level:
    root: INFO
    org.springframework.cloud.sleuth: DEBUG

```

测试，访问：`http://localhost:9001/ribbon/hello/lxy`观察控制台

```text
2019-03-21 10:33:40.746  INFO [eureka-clienta,,,] 18924 --- [nio-9001-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring FrameworkServlet 'dispatcherServlet'
2019-03-21 10:33:40.746  INFO [eureka-clienta,,,] 18924 --- [nio-9001-exec-1] o.s.web.servlet.DispatcherServlet        : FrameworkServlet 'dispatcherServlet': initialization started
2019-03-21 10:33:40.767  INFO [eureka-clienta,,,] 18924 --- [nio-9001-exec-1] o.s.web.servlet.DispatcherServlet        : FrameworkServlet 'dispatcherServlet': initialization completed in 21 ms
2019-03-21 10:33:40.772 DEBUG [eureka-clienta,,,] 18924 --- [nio-9001-exec-1] o.s.c.sleuth.instrument.web.TraceFilter  : Received a request to uri [/ribbon/hello/lxy] that should not be sampled [false]
2019-03-21 10:33:40.778 DEBUG [eureka-clienta,ecced13c679bf567,ecced13c679bf567,false] 18924 --- [nio-9001-exec-1] o.s.c.sleuth.instrument.web.TraceFilter  : No parent span present - creating a new span
2019-03-21 10:33:40.785 DEBUG [eureka-clienta,ecced13c679bf567,ecced13c679bf567,false] 18924 --- [nio-9001-exec-1] o.s.c.s.i.web.TraceHandlerInterceptor    : Handling span [Trace: ecced13c679bf567, Span: ecced13c679bf567, Parent: null, exportable:false]
2019-03-21 10:33:40.785 DEBUG [eureka-clienta,ecced13c679bf567,ecced13c679bf567,false] 18924 --- [nio-9001-exec-1] o.s.c.s.i.web.TraceHandlerInterceptor    : Adding a method tag with value [getHelloMessage] to a span [Trace: ecced13c679bf567, Span: ecced13c679bf567, Parent: null, exportable:false]
2019-03-21 10:33:40.785 DEBUG [eureka-clienta,ecced13c679bf567,ecced13c679bf567,false] 18924 --- [nio-9001-exec-1] o.s.c.s.i.web.TraceHandlerInterceptor    : Adding a class tag with value [RibbonController] to a span [Trace: ecced13c679bf567, Span: ecced13c679bf567, Parent: null, exportable:false]
2019-03-21 10:33:40.805 DEBUG [eureka-clienta,ecced13c679bf567,43c10f3a9c618c12,false] 18924 --- [nio-9001-exec-1] .w.c.AbstractTraceHttpRequestInterceptor : Starting new client span [[Trace: ecced13c679bf567, Span: 43c10f3a9c618c12, Parent: ecced13c679bf567, exportable:false]]
2019-03-21 10:33:40.807  INFO [eureka-clienta,ecced13c679bf567,43c10f3a9c618c12,false] 18924 --- [nio-9001-exec-1] s.c.a.AnnotationConfigApplicationContext : Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@19caab72: startup date [Thu Mar 21 10:33:40 CST 2019]; parent: org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext@4b6690c0
2019-03-21 10:33:40.841  INFO [eureka-clienta,ecced13c679bf567,43c10f3a9c618c12,false] 18924 --- [nio-9001-exec-1] f.a.AutowiredAnnotationBeanPostProcessor : JSR-330 'javax.inject.Inject' annotation found and supported for autowiring
2019-03-21 10:33:40.979  INFO [eureka-clienta,ecced13c679bf567,43c10f3a9c618c12,false] 18924 --- [nio-9001-exec-1] c.netflix.config.ChainedDynamicProperty  : Flipping property: eureka-clientc.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
2019-03-21 10:33:40.992  INFO [eureka-clienta,ecced13c679bf567,43c10f3a9c618c12,false] 18924 --- [nio-9001-exec-1] c.n.u.concurrent.ShutdownEnabledTimer    : Shutdown hook installed for: NFLoadBalancer-PingTimer-eureka-clientc
2019-03-21 10:33:41.007  INFO [eureka-clienta,ecced13c679bf567,43c10f3a9c618c12,false] 18924 --- [nio-9001-exec-1] c.netflix.loadbalancer.BaseLoadBalancer  : Client: eureka-clientc instantiated a LoadBalancer: DynamicServerListLoadBalancer:{NFLoadBalancer:name=eureka-clientc,current list of Servers=[],Load balancer stats=Zone stats: {},Server stats: []}ServerList:null
2019-03-21 10:33:41.011  INFO [eureka-clienta,ecced13c679bf567,43c10f3a9c618c12,false] 18924 --- [nio-9001-exec-1] c.n.l.DynamicServerListLoadBalancer      : Using serverListUpdater PollingServerListUpdater
2019-03-21 10:33:41.026  INFO [eureka-clienta,ecced13c679bf567,43c10f3a9c618c12,false] 18924 --- [nio-9001-exec-1] c.netflix.config.ChainedDynamicProperty  : Flipping property: eureka-clientc.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647
2019-03-21 10:33:41.027  INFO [eureka-clienta,ecced13c679bf567,43c10f3a9c618c12,false] 18924 --- [nio-9001-exec-1] c.n.l.DynamicServerListLoadBalancer      : DynamicServerListLoadBalancer for client eureka-clientc initialized: DynamicServerListLoadBalancer:{NFLoadBalancer:name=eureka-clientc,current list of Servers=[lxy:9003],Load balancer stats=Zone stats: {defaultzone=[Zone:defaultzone;	Instance count:1;	Active connections count: 0;	Circuit breaker tripped count: 0;	Active connections per server: 0.0;]
},Server stats: [[Server:lxy:9003;	Zone:defaultZone;	Total Requests:0;	Successive connection failure:0;	Total blackout seconds:0;	Last connection made:Thu Jan 01 08:00:00 CST 1970;	First connection made: Thu Jan 01 08:00:00 CST 1970;	Active Connections:0;	total failure count in last (1000) msecs:0;	average resp time:0.0;	90 percentile resp time:0.0;	95 percentile resp time:0.0;	min resp time:0.0;	max resp time:0.0;	stddev resp time:0.0]
]}ServerList:org.springframework.cloud.netflix.ribbon.eureka.DomainExtractingServerList@13675903
2019-03-21 10:33:41.090 DEBUG [eureka-clienta,ecced13c679bf567,ecced13c679bf567,false] 18924 --- [nio-9001-exec-1] o.s.c.sleuth.instrument.web.TraceFilter  : Closing the span [Trace: ecced13c679bf567, Span: ecced13c679bf567, Parent: null, exportable:false] since the response was successful
2019-03-21 10:33:41.860 DEBUG [eureka-clienta,,,] 18924 --- [nio-9001-exec-2] o.s.c.sleuth.instrument.web.TraceFilter  : Received a request to uri [/ribbon/hello/lxy] that should not be sampled [false]
2019-03-21 10:33:41.860 DEBUG [eureka-clienta,53424cb5e293614d,53424cb5e293614d,false] 18924 --- [nio-9001-exec-2] o.s.c.sleuth.instrument.web.TraceFilter  : No parent span present - creating a new span
2019-03-21 10:33:41.861 DEBUG [eureka-clienta,53424cb5e293614d,53424cb5e293614d,false] 18924 --- [nio-9001-exec-2] o.s.c.s.i.web.TraceHandlerInterceptor    : Handling span [Trace: 53424cb5e293614d, Span: 53424cb5e293614d, Parent: null, exportable:false]
2019-03-21 10:33:41.861 DEBUG [eureka-clienta,53424cb5e293614d,53424cb5e293614d,false] 18924 --- [nio-9001-exec-2] o.s.c.s.i.web.TraceHandlerInterceptor    : Adding a method tag with value [getHelloMessage] to a span [Trace: 53424cb5e293614d, Span: 53424cb5e293614d, Parent: null, exportable:false]
2019-03-21 10:33:41.861 DEBUG [eureka-clienta,53424cb5e293614d,53424cb5e293614d,false] 18924 --- [nio-9001-exec-2] o.s.c.s.i.web.TraceHandlerInterceptor    : Adding a class tag with value [RibbonController] to a span [Trace: 53424cb5e293614d, Span: 53424cb5e293614d, Parent: null, exportable:false]
2019-03-21 10:33:41.861 DEBUG [eureka-clienta,53424cb5e293614d,d99b3663dfcd9b63,false] 18924 --- [nio-9001-exec-2] .w.c.AbstractTraceHttpRequestInterceptor : Starting new client span [[Trace: 53424cb5e293614d, Span: d99b3663dfcd9b63, Parent: 53424cb5e293614d, exportable:false]]
2019-03-21 10:33:41.867 DEBUG [eureka-clienta,53424cb5e293614d,53424cb5e293614d,false] 18924 --- [nio-9001-exec-2] o.s.c.sleuth.instrument.web.TraceFilter  : Closing the span [Trace: 53424cb5e293614d, Span: 53424cb5e293614d, Parent: null, exportable:false] since the response was successful
2019-03-21 10:33:42.015  INFO [eureka-clienta,,,] 18924 --- [erListUpdater-0] c.netflix.config.ChainedDynamicProperty  : Flipping property: eureka-clientc.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647

Process finished with exit code -1

```
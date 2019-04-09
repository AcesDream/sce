package com.study.sce.eureka.filter;

import com.fasterxml.jackson.databind.ObjectMapper;


import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;


/**
 * 日志记录过滤器
 */
public class ErrorFilter extends ZuulFilter {

    Logger logger = LoggerFactory.getLogger(ErrorFilter.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static ObjectMapper mapper = new ObjectMapper();


    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        //优先级，数字越大，优先级越低
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //是否执行该过滤器，true代表需要过滤
        return true;
    }

    @Override
    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();

        logger.info("进入异常过滤器");

        System.out.println(ctx.getResponseBody());

        ctx.setResponseBody("出现异常IN ErrorFilter");

        return null;
    }
}

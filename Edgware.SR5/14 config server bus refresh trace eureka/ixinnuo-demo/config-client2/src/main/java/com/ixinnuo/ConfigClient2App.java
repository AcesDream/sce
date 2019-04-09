package com.ixinnuo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 配置中心!
 *
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ConfigClient2App
{
    public static void main( String[] args )
    {
        SpringApplication.run(ConfigClient2App.class, args);
    }
}

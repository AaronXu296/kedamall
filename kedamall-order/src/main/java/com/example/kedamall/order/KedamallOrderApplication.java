package com.example.kedamall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRetry
@EnableDiscoveryClient
@EnableRedisHttpSession
@EnableFeignClients
//@EnableAspectJAutoProxy(exposeProxy = true)
public class KedamallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(KedamallOrderApplication.class, args);
    }

}

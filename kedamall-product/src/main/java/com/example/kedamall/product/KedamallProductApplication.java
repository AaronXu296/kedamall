package com.example.kedamall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan(value = "com.example.kedamall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.example.kedamall.product.feign")
public class KedamallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(KedamallProductApplication.class, args);
    }

}

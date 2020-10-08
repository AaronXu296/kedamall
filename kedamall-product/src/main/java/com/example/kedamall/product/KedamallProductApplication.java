package com.example.kedamall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan(value = "com.example.kedamall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class KedamallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(KedamallProductApplication.class, args);
    }

}

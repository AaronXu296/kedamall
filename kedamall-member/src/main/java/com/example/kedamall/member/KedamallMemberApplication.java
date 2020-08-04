package com.example.kedamall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.kedamall.member.feign")
public class KedamallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(KedamallMemberApplication.class, args);
    }

}

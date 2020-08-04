package com.example.kedamall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class KedamallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(KedamallCouponApplication.class, args);
    }

}

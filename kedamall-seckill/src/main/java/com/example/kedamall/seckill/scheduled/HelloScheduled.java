package com.example.kedamall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
//@EnableScheduling
@Slf4j
public class HelloScheduled {
    /**
     *
     */
    @Scheduled(cron = " * * * ? * 5")
    public void hello(){
        log.info("hello......");
    }
}

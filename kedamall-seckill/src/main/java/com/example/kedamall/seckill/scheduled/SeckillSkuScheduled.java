package com.example.kedamall.seckill.scheduled;

import com.example.kedamall.seckill.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品定时上架
 * 每晚3点，上架最近3天要秒杀的商品（预告）
 */
@Service
@Slf4j
public class SeckillSkuScheduled {

    @Autowired
    SecKillService secKillService;

    @Autowired
    RedissonClient redissonClient;

    private final String upload_lock = "seckill:upload:lock";


    /**
     * TODO 保证上架的幂等性
     */
    @Scheduled(cron = "*/30 * * * * ?")
    public void uploadSeckillSkuLatest3Days(){

        log.info("上架秒杀的商品信息");

        //分布式锁
        RLock lock = redissonClient.getLock(upload_lock);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            secKillService.uploadSeckillSkuLatest3Days();
        }finally {
            lock.unlock();
        }
    }
}

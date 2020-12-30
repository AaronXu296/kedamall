package com.example.kedamall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.common.to.mq.SeckillOrderTo;
import com.example.common.utils.R;
import com.example.common.vo.MemberResponseVo;
import com.example.kedamall.seckill.feign.CouponFeignService;
import com.example.kedamall.seckill.feign.ProductFeignService;
import com.example.kedamall.seckill.interceptor.LoginUserInterceptor;
import com.example.kedamall.seckill.service.SecKillService;
import com.example.kedamall.seckill.to.SecKillSkuRedisTo;
import com.example.kedamall.seckill.vo.SeckillSessionsWithSkus;
import com.example.kedamall.seckill.vo.SeckillSkuVo;
import com.example.kedamall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.xml.crypto.Data;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeckillServiceImpl implements SecKillService {
    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";

    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";

    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";//+商品随机码

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void uploadSeckillSkuLatest3Days() {
        //1.数据库扫描：最近三天需要参与秒杀的活动
        R r = couponFeignService.getLatest3DaySession();
        if(r.getCode()==0){

            //1.上架商品;
            List<SeckillSessionsWithSkus> sessionData = r.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });
            if(!CollectionUtils.isEmpty(sessionData)){
                //2.商品缓存到redis
                //2.1缓存活动信息
                saveSessionInfos(sessionData);

                //2.2缓存活动的关联商品信息
                saveSessionSkuInfo(sessionData);
            }
        }
    }

    public List<SecKillSkuRedisTo> blockHandler(BlockException e){
        log.error("getCurrentSeckillSkus被降级");
        return null;
    }
    /**
     * 获取当前时间可以参加的秒杀时间
     * @return
     */
    @Override
    @SentinelResource(value = "getCurrentSeckillSkusResource",blockHandler = "blockHandler")
    public List<SecKillSkuRedisTo> getCurrentSeckillSkus() {
        //确定当前时间属于哪个场次
        long time = new Date().getTime();
        try(Entry entry = SphU.entry("seckillSkus");) {
            Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
            for (String key : keys) {
                //seckill:sessions:1609113600000_1609120800000
                String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                long start = Long.parseLong(s[0]);
                long end = Long.parseLong(s[1]);
                if(time>=start && time<=end){
                    //获取当前场次所有的商品信息
                    List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                    BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                    List<String> list = hashOps.multiGet(range);
                    if(list!=null){
                        List<SecKillSkuRedisTo> collect = list.stream().map(item -> {
                            SecKillSkuRedisTo to = JSON.parseObject(item, SecKillSkuRedisTo.class);
                            return to;
                        }).collect(Collectors.toList());
                        return collect;
                    }
                }
            }
        }catch (BlockException e){
            log.error("资源被限流");
        }

        return null;
    }

    @Override
    public SecKillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        //找到所有需要参与秒杀的商品的Key信息（seckill:skus是一个Hash结构）
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if(!CollectionUtils.isEmpty(keys)){
            //正则表达式匹配
            String reg = "\\d_"+skuId;
            for (String key : keys) {
                boolean matches = Pattern.matches(reg, key);
                if(matches){
                    String json = hashOps.get(key);
                    SecKillSkuRedisTo redisTo = JSON.parseObject(json, SecKillSkuRedisTo.class);
                    //处理随机码：
                    //如果在秒杀时间内：则不处理
                    //否则不显示随机码
                    long time = new Date().getTime();
                    if(time<redisTo.getStartTime() && time>redisTo.getEndTime()){
                        redisTo.setRandomCode(null);
                    }
                    return redisTo;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) {
        //1.获取秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

        String s = hashOps.get(killId);
        if(StringUtils.isEmpty(s)){
            return null;
        }else {
            SecKillSkuRedisTo redisTo = JSON.parseObject(s, SecKillSkuRedisTo.class);
            //2.合法性校验
            //2.1秒杀时间校验
            Long startTime = redisTo.getStartTime();
            Long endTime = redisTo.getEndTime();
            long time = new Date().getTime();
            long ttl = endTime - startTime;
            if(time<startTime && time>endTime){
                return null;
            }
            //2.2随机码校验和商品ID
            String randomCode = redisTo.getRandomCode();
            String id = redisTo.getPromotionSessionId() + "_" + redisTo.getSkuId();
            if(!key.equals(randomCode) || !killId.equals(id)){
                return null;
            }
            //2.3购买数量是否超过限购数量
            if(num>redisTo.getSeckillLimit().intValue())return null;
            //2.4验证这个人是否购买过了（幂等性）==》只要秒杀成功就去redis占位；数据格式 userId_sessionId_skuId
            MemberResponseVo memberResponseVo = LoginUserInterceptor.loginUser.get();
            String occupyKey = memberResponseVo.getId() + "_" + id;
            Boolean absent = redisTemplate.opsForValue().setIfAbsent(occupyKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
            if(!absent){
                //占位失败：已买过
                return null;
            }

            //TODO 3.开始秒杀！！！！
            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
            boolean acquire = semaphore.tryAcquire(num);
            //只要信号量获取成功
            if(acquire){
                // 秒杀成功 快速下单 发送消息到 MQ 整个操作时间在 10ms 左右
                String timeId = IdWorker.getTimeId();
                SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
                seckillOrderTo.setOrderSn(timeId);
                seckillOrderTo.setMemberId(memberResponseVo.getId());
                seckillOrderTo.setNum(num);
                seckillOrderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                seckillOrderTo.setSkuId(redisTo.getSkuId());
                seckillOrderTo.setSeckillPrice(redisTo.getSeckillPrice());
                rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", seckillOrderTo);
                return timeId;
            }
        }
        return null;
    }

    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions){
        sessions.stream().forEach(session->{
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
            Boolean hasKey = redisTemplate.hasKey(key);
            if(!hasKey){
                List<String> collect = session.getRelationSkus().stream().
                        map(item ->
                                item.getPromotionId() + "_" + item.getSkuId().toString()).
                        collect(Collectors.toList());
                //缓存活动信息
                redisTemplate.opsForList().leftPushAll(key,collect);
            }



        });
    }

    private void saveSessionSkuInfo(List<SeckillSessionsWithSkus> sessions){
        sessions.stream().forEach(session->{
            //准备哈希操作
            BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                Boolean hasKey = hashOps.hasKey(seckillSkuVo.getPromotionId() + "_" + seckillSkuVo.getSkuId().toString());
                //设置商品的随机码
                if(!hasKey){
                    String token = UUID.randomUUID().toString().replace("-", "");
                    SecKillSkuRedisTo redisTo = new SecKillSkuRedisTo();
                    //sku的基本数据
                    R r = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                    if(r.getCode()==0){
                        SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        redisTo.setSkuInfo(skuInfo);
                    }
                    //sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo,redisTo);

                    //设置当前秒杀商品的的开始、结束时间
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());

                    redisTo.setRandomCode(token);

                    String s = JSON.toJSONString(redisTo);
                    hashOps.put(seckillSkuVo.getPromotionId() + "_" + seckillSkuVo.getSkuId().toString(),s);


                    //当前场次库存信息是否已上架？
                    //使用库存作为分布式信号量 （限流）
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    //商品可以秒杀的数量作为信号量
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount().intValue());
                }
            });
        });
    }

}

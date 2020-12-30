package com.example.kedamall.order.config;

import com.example.kedamall.order.entity.OrderEntity;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyMQConfig {


    /**
     * 容器中的 Binding、Queue、exchange 都会自动创建，(RabbitMQ没有的情况下)
     * @return
     */
    @Bean
    public Queue orderDelayQueue(){
        // 特殊参数
        Map<String,Object> map = new HashMap<>();
        // 设置交换器

        map.put("x-dead-letter-exchange", "order-event-exchange");
        // 路由键
        map.put("x-dead-letter-routing-key","order.release.order");
        // 消息过期时间
        map.put("x-message-ttl",60000);
        Queue queue = new Queue("order.delay.queue", true, false, false,map);
        return queue;
    }

    /**
     * 创建队列
     * @return
     */
    @Bean
    public Queue orderReleaseOrderQueue() {
        Queue queue = new Queue("order.release.order.queue", true, false, false);
        return queue;
    }

    /**
     * 创建交换机
     * @return
     */
    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange("order-event-exchange",true,false);
    }

    /**
     * 绑定关系 将delay.queue和event-exchange进行绑定
     * @return
     */
    @Bean
    public Binding orderCreateOrderBingding(){
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }

    /**
     * 将 release.queue 和 event-exchange进行绑定
     * @return
     */
    @Bean
    public Binding orderReleaseOrderBinding(){
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }

    @Bean
    public Binding orderReleaseOtherBinding(){
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }

    @Bean
    public Queue orderSeckillOrderQueue() {
        return new Queue("order.seckill.order.queue", true, false, false);
    }

    @Bean
    public Binding orderSeckillOrderQueueBinding() {
        return new Binding("order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.seckill.order",
                new HashMap<>());
    }
}

package com.example.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillOrderTo {
    private String orderSn;

    private Long promotionSessionId; // 秒杀活动批次

    private Long skuId;

    private BigDecimal seckillPrice;

    private Integer num;

    private Long memberId;
}

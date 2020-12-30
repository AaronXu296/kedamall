package com.example.kedamall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSubmitVo {
    //无需提交要购买的商品，去购物车再查一次
    //用户的信息也都在session中
    private Long addrId;

    private Integer payType; // 支付方式

    private String orderToken; //放重令牌

    private BigDecimal payPrice; // 应付价格 （用于验价）

    private String note; // 订单备注
}

package com.example.kedamall.order.to;

import com.example.kedamall.order.entity.OrderEntity;
import com.example.kedamall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateTo {
    private OrderEntity orderEntity;

    private List<OrderItemEntity> orderItems;

    private BigDecimal payPrice;

    private BigDecimal fare;
}

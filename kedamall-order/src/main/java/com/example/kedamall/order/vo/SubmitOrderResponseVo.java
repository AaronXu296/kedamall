package com.example.kedamall.order.vo;

import com.example.kedamall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {
    private OrderEntity orderEntity;

    //0代表成功
    private Integer code;
}

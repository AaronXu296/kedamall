package com.example.kedamall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.to.mq.SeckillOrderTo;
import com.example.common.utils.PageUtils;
import com.example.kedamall.order.entity.OrderEntity;
import com.example.kedamall.order.vo.OrderConfirmVo;
import com.example.kedamall.order.vo.OrderSubmitVo;
import com.example.kedamall.order.vo.PayVo;
import com.example.kedamall.order.vo.SubmitOrderResponseVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author AaronXu296
 * @email xzy2967@163.com
 * @date 2020-08-02 14:48:58
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    OrderEntity getOrderStatus(String orderSn);

    void closeOrder(OrderEntity entity);

    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * 获取当前订单的支付信息
     * @param orderSn
     * @return
     */
    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    void createSeckillOrder(SeckillOrderTo seckillOrder);
}


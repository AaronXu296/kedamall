package com.example.kedamall.order.dao;

import com.example.kedamall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author AaronXu296
 * @email xzy2967@163.com
 * @date 2020-08-02 14:48:58
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}

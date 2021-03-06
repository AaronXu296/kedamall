package com.example.kedamall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.to.mq.OrderTo;
import com.example.common.to.mq.StockLockedTo;
import com.example.common.utils.PageUtils;
import com.example.kedamall.ware.entity.WareSkuEntity;
import com.example.kedamall.ware.vo.SkuHasStockVo;
import com.example.kedamall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author AaronXu296
 * @email xzy2967@163.com
 * @date 2020-08-02 14:56:01
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    Boolean orderLockStock(WareSkuLockVo vo);

    void unlockStock(StockLockedTo to);

    void unlockStock(OrderTo orderTo);
}


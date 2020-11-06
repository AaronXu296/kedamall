package com.example.kedamall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.to.SkuReductionTo;
import com.example.common.utils.PageUtils;
import com.example.kedamall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author AaronXu296
 * @email xzy2967@163.com
 * @date 2020-08-02 14:31:40
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo reductionTo);
}


package com.example.kedamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.kedamall.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author AaronXu296
 * @email xzy2967@163.com
 * @date 2020-08-04 15:04:25
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

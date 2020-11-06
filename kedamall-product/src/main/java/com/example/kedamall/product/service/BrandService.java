package com.example.kedamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.kedamall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author AaronXu296
 * @email xzy2967@163.com
 * @date 2020-08-04 15:04:26
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);
}


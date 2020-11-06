package com.example.kedamall.coupon.service.impl;

import com.example.common.to.SpuBoundsTo;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.kedamall.coupon.dao.SpuBoundsDao;
import com.example.kedamall.coupon.entity.SpuBoundsEntity;
import com.example.kedamall.coupon.service.SpuBoundsService;


@Service("spuBoundsService")
public class SpuBoundsServiceImpl extends ServiceImpl<SpuBoundsDao, SpuBoundsEntity> implements SpuBoundsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuBoundsEntity> page = this.page(
                new Query<SpuBoundsEntity>().getPage(params),
                new QueryWrapper<SpuBoundsEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void save(SpuBoundsTo spuBoundsTo) {
        this.save(spuBoundsTo);
    }

}

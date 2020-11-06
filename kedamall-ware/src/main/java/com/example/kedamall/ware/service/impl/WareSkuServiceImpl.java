package com.example.kedamall.ware.service.impl;

import com.example.common.utils.R;
import com.example.kedamall.ware.feign.ProductFeignService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.kedamall.ware.dao.WareSkuDao;
import com.example.kedamall.ware.entity.WareSkuEntity;
import com.example.kedamall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //判断如果没有这个库存记录 则新增
        List<WareSkuEntity> entities = this.baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (entities==null || entities.size()==0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(0);
            //TODO 查询sku名字,若失败，整个事务不必回滚
            try {
                R info = productFeignService.info(skuId);
                Map<String,Object> skuInfo = (Map<String, Object>) info.get("skuInfo");

                if(info.getCode()==0){
                    wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
                }
            }catch (Exception e){
                //不抛出 就不回滚！
            }
            this.baseMapper.insert(wareSkuEntity);
        }
        this.baseMapper.addStock(skuId,wareId,skuNum);
    }

}

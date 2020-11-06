package com.example.kedamall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.kedamall.product.dao.SpuImagesDao;
import com.example.kedamall.product.entity.SpuImagesEntity;
import com.example.kedamall.product.service.SpuImagesService;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveImages(Long infoId, List<String> images) {
        if(images!=null || images.size()!=0){
            List<SpuImagesEntity> collect = images.stream().map((image) -> {
                SpuImagesEntity imagesEntity = new SpuImagesEntity();
                imagesEntity.setSpuId(infoId);
                imagesEntity.setImgUrl(image);
                return imagesEntity;
            }).collect(Collectors.toList());
            this.saveBatch(collect);
        }
    }

}

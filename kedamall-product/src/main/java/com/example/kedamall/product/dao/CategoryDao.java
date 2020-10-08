package com.example.kedamall.product.dao;

import com.example.kedamall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author AaronXu296
 * @email xzy2967@163.com
 * @date 2020-08-04 15:04:24
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}

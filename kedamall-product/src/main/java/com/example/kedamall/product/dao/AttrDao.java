package com.example.kedamall.product.dao;

import com.example.kedamall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author AaronXu296
 * @email xzy2967@163.com
 * @date 2020-08-04 15:04:27
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectSearchAttrsIds(@Param("attrIds") List<Long> attrIds);
}

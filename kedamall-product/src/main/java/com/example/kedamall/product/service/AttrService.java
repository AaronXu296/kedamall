package com.example.kedamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.kedamall.product.entity.AttrEntity;
import com.example.kedamall.product.vo.AttrGroupRelationVo;
import com.example.kedamall.product.vo.AttrRespVo;
import com.example.kedamall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author AaronXu296
 * @email xzy2967@163.com
 * @date 2020-08-04 15:04:27
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attrVo);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attrVo);

    List<AttrEntity> getRelationAttr(Long attrGroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * 挑出检索属性
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrsIds(List<Long> attrIds);
}


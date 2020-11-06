package com.example.kedamall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.example.kedamall.product.entity.ProductAttrValueEntity;
import com.example.kedamall.product.service.ProductAttrValueService;
import com.example.kedamall.product.vo.AttrRespVo;
import com.example.kedamall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.kedamall.product.entity.AttrEntity;
import com.example.kedamall.product.service.AttrService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;

import javax.management.AttributeList;


/**
 * 商品属性
 *
 * @author AaronXu296
 * @email xzy2967@163.com
 * @date 2020-08-04 15:04:27
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;
    // /product/attr/info/{attrId}

    // /product/attr/sale/list

    // /product/attr/base/listforspu/{spuId}

    // /product/attr/update/{spuId}
    @PostMapping("/update/{spuId}")
    //@RequiresPermissions("product:attr:update")
    public R updateSpuAttr(@PathVariable("spuId") Long spuId, @RequestBody List<ProductAttrValueEntity> entities){
        productAttrValueService.updateSpuAttr(spuId,entities);
        return R.ok();
    }


    @GetMapping("/base/listforspu/{catelogId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> attrValueEntities = productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data",attrValueEntities);
    }

    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable(value = "catelogId")Long catelogId,
                          @PathVariable(value = "attrType")String type){
        PageUtils page = attrService.queryBaseAttrPage(params,catelogId,type);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		AttrEntity attr = attrService.getById(attrId);
        AttrRespVo respVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attrVo){
		attrService.saveAttr(attrVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attrVo){
		attrService.updateAttr(attrVo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}

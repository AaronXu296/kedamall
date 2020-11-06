package com.example.kedamall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.example.kedamall.product.dao.AttrGroupDao;
import com.example.kedamall.product.entity.AttrEntity;
import com.example.kedamall.product.service.AttrAttrgroupRelationService;
import com.example.kedamall.product.service.AttrService;
import com.example.kedamall.product.service.CategoryService;
import com.example.kedamall.product.vo.AttrGroupRelationVo;
import com.example.kedamall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.kedamall.product.entity.AttrGroupEntity;
import com.example.kedamall.product.service.AttrGroupService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 属性分组
 *
 * @author AaronXu296
 * @email xzy2967@163.com
 * @date 2020-08-04 15:04:26
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    AttrAttrgroupRelationService relationService;

    // /product/attrgroup/{catelogId}/withattr
    @GetMapping(value = "/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable(value = "catelogId")Long catelogId){
        //1、查出当前分类下的所有属性分组
        //2、查出每个属性分组下的所有属性
        List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrsByCatId(catelogId);
        return R.ok().put("data",vos);
    }


    // /product/attrgroup/attr/relation
    @PostMapping(value = "/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos){
        relationService.saveBatch(vos);
        return R.ok();
    }


    // /product/attrgroup/{attrgroupId}/noattr/relation
    @GetMapping(value = "/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String, Object> params,
                            @PathVariable(value = "attrgroupId")Long attrgroupId){
        PageUtils page = attrService.getNoRelationAttr(params,attrgroupId);
        return R.ok().put("data",page);
    }

    @PostMapping(value = "/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos){
        attrService.deleteRelation(vos);
        return R.ok();
    }


    @GetMapping(value = "/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable(value = "attrgroupId")Long attrgroupId){
        List<AttrEntity> entities = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data",entities);
    }


    /**
     * 列表
     */
    @RequestMapping("/list/{categoryId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable(value = "categoryId") Long categoryId){
        //PageUtils page = attrGroupService.queryPage(params);

        PageUtils page = attrGroupService.queryPage(params,categoryId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}

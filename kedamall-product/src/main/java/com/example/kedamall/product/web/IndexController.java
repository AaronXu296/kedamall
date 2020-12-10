package com.example.kedamall.product.web;

import com.example.kedamall.product.entity.CategoryEntity;
import com.example.kedamall.product.service.CategoryService;
import com.example.kedamall.product.vo.Catelog2Vo;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    RedissonClient redisson;

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/","index.html"})
    public String indexPage(Model model){
        //TODO 1、查出所有一级分类
        List<CategoryEntity> categories = categoryService.getLevel1Categorys();
        model.addAttribute("categories",categories);
        return "index";
    }

    @ResponseBody
    @GetMapping(value = "/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson(){
        Map<String, List<Catelog2Vo>> catelogJson = categoryService.getCatelogJson();
        return catelogJson;
    }
}

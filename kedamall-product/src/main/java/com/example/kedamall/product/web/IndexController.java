package com.example.kedamall.product.web;

import com.example.kedamall.product.entity.CategoryEntity;
import com.example.kedamall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/","index.html"})
    public String indexPage(Model model){
        //TODO 1、查出所有一级分类
        List<CategoryEntity> categories = categoryService.getLevel1Categorys();
        model.addAttribute("categories",categories);
        return "index";
    }
}

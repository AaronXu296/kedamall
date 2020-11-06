package com.example.kedamall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.kedamall.product.entity.BrandEntity;
import com.example.kedamall.product.service.BrandService;
import com.example.kedamall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Slf4j
@SpringBootTest
class KedamallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Test
    public void testFindPath(){
        Long[] path = categoryService.findCatelogPath(225L);
        log.info("完整路径：{}", Arrays.asList(path));
    }


    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("保存成功...");
//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("made in huawei");
//
//        brandService.updateById(brandEntity);
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        Iterator<BrandEntity> iterator = list.iterator();

        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }

}

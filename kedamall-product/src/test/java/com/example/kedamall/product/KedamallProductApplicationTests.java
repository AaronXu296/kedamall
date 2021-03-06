package com.example.kedamall.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.kedamall.product.dao.SkuSaleAttrValueDao;
import com.example.kedamall.product.entity.BrandEntity;
import com.example.kedamall.product.service.AttrGroupService;
import com.example.kedamall.product.service.BrandService;
import com.example.kedamall.product.service.SkuSaleAttrValueService;
import com.example.kedamall.product.vo.SpuItemAttrGroupVo;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class KedamallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public void saleAttrTe(){
        System.out.println(skuSaleAttrValueDao.getSaleAttrsBySpuId(13L));
    }

    @Test
    public void RedissonTest(){
        System.out.println(redissonClient);
    }

    @Test
    public void testGroup(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupService.getAttrGroupWithAttrsBySpuId(13L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }

    @Test
    public void RedisTest(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello","world"+ UUID.randomUUID().toString());

        //
        System.out.println("之前保存的数据是"+ops.get("hello"));
    }

    @Test
    public void testUpload() throws FileNotFoundException {
//        // Endpoint以杭州为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-chengdu.aliyuncs.com";
//        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
//        String accessKeyId = "LTAI4GAZUbcXaQ7w812Ujaqr";
//        String accessKeySecret = "7hwlOA52Wx6JeJjW9jzPESYeMgD6qm";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId,accessKeySecret);

        // 上传Byte数组。
        InputStream inputStream = new FileInputStream("C:\\Users\\Aaron\\Desktop\\图片\\figure_paper.png");
        // 关闭OSSClient。
        System.out.println("上传成功");
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

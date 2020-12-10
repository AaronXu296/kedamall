package com.example.kedamall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.kedamall.product.service.CategoryBrandRelationService;
import com.example.kedamall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.kedamall.product.dao.CategoryDao;
import com.example.kedamall.product.entity.CategoryEntity;
import com.example.kedamall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 这个类继承了 ServiceImpl
        // 1. 查出所有分类列表
        List<CategoryEntity> entities = baseMapper.selectList(null); // 传入 null 代表查询所有

        // 2. 组装成树形结构
        List<CategoryEntity> levelMenu = entities.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(menu -> {
                    menu.setChildren(getChildren(menu, entities));
                    return menu;
                }).sorted((m1, m2) -> m1.getSort() == null ? 0 : m1.getSort() - (m2.getSort() == null ? 0 : m2.getSort())).collect(Collectors.toList());
        return levelMenu;
    }


    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == root.getCatId())
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildren(categoryEntity, all));
                    return categoryEntity;
                })
                .sorted((m1, m2) -> m1.getSort() == null ? 0 : m1.getSort() - (m2.getSort() == null ? 0 : m2.getSort()))
                .collect(Collectors.toList());
        return children;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查当前要删除的菜单是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        findParentPath(catelogId, paths);
        Collections.reverse(paths);

        return (Long[]) paths.toArray(new Long[paths.size()]);
    }

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
    @Transactional
    @Override

    @Caching(evict = {
            @CacheEvict(value = "category",key = "'getLevel1Categorys'"),
            @CacheEvict(value = "category", key = "'getCatelogJson'")
    })
    // @CacheEvict(value = "category", allEntries = true)
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    @Cacheable(cacheNames = {"category"}, key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("getLevel1Categorys................");
        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    @Cacheable(value = "category", key = "#root.method.name")
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(catelog -> catelog.getCatId().toString(), v -> {
            //查到每个一级分类下的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;

            //封装成Catelog2Vo
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().
                        map(category2 -> {
                                    List<CategoryEntity> category3 = getParent_cid(selectList, category2.getCatId());
                                    List<Catelog2Vo.Catalog3Vo> collect = null;
                                    if (category3 != null) {
                                        //category3封装成指定形式
                                        collect = category3.stream().map(c3 -> {
                                            Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.
                                                    Catalog3Vo(category2.getCatId().toString(), c3.getCatId().toString(), c3.getName());
                                            return catalog3Vo;
                                        }).collect(Collectors.toList());
                                    }
                                    return new Catelog2Vo(v.getCatId().toString(),
                                            category2.getCatId().toString(),
                                            category2.getName(),
                                            collect);
                                }
                        ).collect(Collectors.toList());
                //当前二级分类下的三级分类封装成vo
            }
            return catelog2Vos;
        }));

        return parent_cid;
    }


    public Map<String, List<Catelog2Vo>> getCatelogJson2() {
        //1、加入缓存逻辑，缓存中存的所有数据都是json字符串
        //json跨语言跨平台！！！
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            //2、如果缓存中没有，则查询数据库
            System.out.println("缓存不命中，查询数据库.....");
            Map<String, List<Catelog2Vo>> catelogJsonFromDB = getCatelogJsonFromDBWithRedissonLock();
            return catelogJsonFromDB;
        }
        System.out.println("缓存命中，直接返回.....");
        Map<String, List<Catelog2Vo>> result = JSON.
                parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
        return result;
    }

    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDBWithRedissonLock() {

        //占分布式锁
        RLock lock = redisson.getLock("CatelogJson-Lock");
        lock.lock();

        Map<String, List<Catelog2Vo>> dataFromDB = null;
        try {
            dataFromDB = getDataFromDB();
        } finally {
            lock.unlock();
        }

        return dataFromDB;
    }

    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDBWithRedisLock() {

        //占分布式锁
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功");
            //加锁成功
            //设置过期时间
            //redisTemplate.expire("lock",30,TimeUnit.SECONDS);
            Map<String, List<Catelog2Vo>> dataFromDB = null;
            try {
                dataFromDB = getDataFromDB();
            } finally {
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                        Arrays.asList("lock"), uuid);
            }
//            String lockValue = redisTemplate.opsForValue().get("lock");
//            if(uuid.equals(lockValue)){
//                //删除我自己的锁
//                redisTemplate.delete("lock");
//            }
            return dataFromDB;
        } else {
            //加锁失败......重试（自旋的方式）
            //休眠100ms
            System.out.println("获取分布式锁失败.......等待再次获取");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatelogJsonFromDBWithRedisLock();
        }
    }

    private Map<String, List<Catelog2Vo>> getDataFromDB() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            Map<String, List<Catelog2Vo>> result = JSON.
                    parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                    });
            return result;
        }
        System.out.println("查询了数据库.....");
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(catelog -> catelog.getCatId().toString(), v -> {
            //查到每个一级分类下的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;

            //封装成Catelog2Vo
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().
                        map(category2 -> {
                                    List<CategoryEntity> category3 = getParent_cid(selectList, category2.getCatId());
                                    List<Catelog2Vo.Catalog3Vo> collect = null;
                                    if (category3 != null) {
                                        //category3封装成指定形式
                                        collect = category3.stream().map(c3 -> {
                                            Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.
                                                    Catalog3Vo(category2.getCatId().toString(), c3.getCatId().toString(), c3.getName());
                                            return catalog3Vo;
                                        }).collect(Collectors.toList());
                                    }
                                    return new Catelog2Vo(v.getCatId().toString(),
                                            category2.getCatId().toString(),
                                            category2.getName(),
                                            collect);
                                }
                        ).collect(Collectors.toList());
                //当前二级分类下的三级分类封装成vo
            }
            return catelog2Vos;
        }));
        //3、查到的数据放入缓存,再转为json存放
        String s = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
        return parent_cid;
    }

    //从数据库查询封装数据
    //TODO 本地锁只锁当前进程，分布式情况下要使用分布式锁
    public synchronized Map<String, List<Catelog2Vo>> getCatelogJsonFromDBWithLocalLock() {
        return getDataFromDB();
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
    }

    public void findParentPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);

        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
    }

}

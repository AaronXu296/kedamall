package com.example.kedamall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.example.common.utils.R;
import com.example.kedamall.ware.feign.MemberFeignService;
import com.example.kedamall.ware.vo.FareVo;
import com.example.kedamall.ware.vo.MemberAddressVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.kedamall.ware.dao.WareInfoDao;
import com.example.kedamall.ware.entity.WareInfoEntity;
import com.example.kedamall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.eq("id",key).or().
                    like("name",key).or().
                    like("address",key).or().like("areacode",key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        R r = memberFeignService.addrInfo(addrId);
        FareVo fareVo = new FareVo();
        MemberAddressVo addressVo = r.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>() {
        });
        if(addressVo!=null){
            String phone = addressVo.getPhone();
            String fare = phone.substring(phone.length() - 1, phone.length());

            fareVo.setAddress(addressVo);
            fareVo.setFare(new BigDecimal(fare));
            return fareVo;
        }
        return null;
    }


}

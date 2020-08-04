package com.example.kedamall.member.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "kedamall-coupon")
public interface CouponFeignService {

    @RequestMapping(value = "/coupon/coupon/member/list")
    public R membercoupons();
}

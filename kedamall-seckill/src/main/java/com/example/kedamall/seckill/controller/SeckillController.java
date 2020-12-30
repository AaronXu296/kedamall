package com.example.kedamall.seckill.controller;

import com.example.common.utils.R;
import com.example.kedamall.seckill.service.SecKillService;
import com.example.kedamall.seckill.to.SecKillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SeckillController {
    @Autowired
    SecKillService secKillService;

    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        List<SecKillSkuRedisTo> tos = secKillService.getCurrentSeckillSkus();
        return R.ok().setData(tos);
    }

    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId){
        SecKillSkuRedisTo to = secKillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(to);
    }

    @GetMapping("/kill")
    public String secKill(@RequestParam("killId") String killId,
                     @RequestParam("key") String key,
                     @RequestParam("num") String num,
                     Model model){
        // Interceptor 拦截器判断是否登录

        //秒杀成功就返回一个订单号
        String orderSn = secKillService.kill(killId,key,Integer.parseInt(num));
        model.addAttribute("orderSn", orderSn);
        return "success";
    }
}

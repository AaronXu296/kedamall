package com.example.kedamall.order.web;

import com.alipay.api.AlipayApiException;
import com.example.kedamall.order.config.AlipayTemplate;
import com.example.kedamall.order.service.OrderService;
import com.example.kedamall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PayWebController {

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

//    @ResponseBody
//    @GetMapping(value = "/payOrder",produces = "text/html")
//    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
//
//        PayVo payVo = orderService.getOrderPay(orderSn);
//        String pay = alipayTemplate.pay(payVo);
//        //返回的是一个页面，应将此页面直接交给浏览器
//        System.out.println(pay);
//        return pay;
//    }

    //@ResponseBody
    @GetMapping(value = "/payOrder",produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

        PayVo payVo = orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);
        //返回的是一个页面，应将此页面直接交给浏览器
        System.out.println(pay);
        return "redirect:http://member.kedamall.com/memberOrder.html";
    }

}

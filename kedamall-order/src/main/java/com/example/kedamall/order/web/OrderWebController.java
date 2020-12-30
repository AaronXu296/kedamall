package com.example.kedamall.order.web;

import com.example.common.exception.NoStockException;
import com.example.kedamall.order.service.OrderService;
import com.example.kedamall.order.vo.OrderConfirmVo;
import com.example.kedamall.order.vo.OrderSubmitVo;
import com.example.kedamall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData",orderConfirmVo);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){
        try {
            //创建订单、验令牌、验价格、锁库存.......
            SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
            //成功则来到支付页
            //失败回到失败页
            if(responseVo.getCode()==0) {
                //成功则来到支付页
                model.addAttribute("submitOrderResponse", responseVo);
                return "pay";
            }else {
                //失败回到失败页
                String msg = "下单失败；";
                switch (responseVo.getCode()){
                    case 1:msg+="令牌校验失败";break;
                    case 2:msg+="库存锁定失败（库存不足）";
                }
                redirectAttributes.addFlashAttribute("msg",msg);
                return "redirect:http://order.kedamall.com/toTrade";
            }

        } catch (Exception e){
            if (e instanceof NoStockException) {
                String message = e.getMessage();
                redirectAttributes.addFlashAttribute("msg", message);
            }
            return "redirect:http://order.kedamall.com/toTrade";
        }
    }

}

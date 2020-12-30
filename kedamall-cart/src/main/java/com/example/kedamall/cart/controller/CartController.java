package com.example.kedamall.cart.controller;

import com.example.common.constant.AuthServerConstant;
import com.example.kedamall.cart.interceptor.CartInterceptor;
import com.example.kedamall.cart.service.CartService;
import com.example.kedamall.cart.vo.CartItemVo;
import com.example.kedamall.cart.vo.CartVo;
import com.example.kedamall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItemVo> getCurrentUserCartItems(){
        return cartService.getCurrentUserCartItems();
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.kedamall.com//cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num")Integer num){
        cartService.changeItemCount(skuId,num);
        return "redirect:http://cart.kedamall.com//cart.html";
    }

    @GetMapping("/checkItem")
    public String checkItemId(@RequestParam("skuId") Long skuId, @RequestParam("checked")Integer check){
        cartService.checkItem(skuId,check);
        return "redirect:http://cart.kedamall.com//cart.html";
    }

    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        CartVo cart = cartService.getCart();
        BigDecimal totalAmount = cart.getTotalAmount();
        model.addAttribute("cart",cart);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     * @return
     */
    @GetMapping("addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        CartItemVo cartItemVo = cartService.addToCart(skuId,num);

        //放到URL后面当作参数
        redirectAttributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.kedamall.com/addToCartSuccess.html";
    }

    /**
     * 跳转到成功页
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model){
        CartItemVo item = cartService.getCartItem(skuId);
        model.addAttribute("item",item);
        return "success";
    }
}

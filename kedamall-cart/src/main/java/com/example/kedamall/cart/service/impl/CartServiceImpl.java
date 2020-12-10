package com.example.kedamall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.common.utils.R;
import com.example.kedamall.cart.feign.ProductFeignService;
import com.example.kedamall.cart.interceptor.CartInterceptor;
import com.example.kedamall.cart.service.CartService;
import com.example.kedamall.cart.vo.CartItemVo;
import com.example.kedamall.cart.vo.CartVo;
import com.example.kedamall.cart.vo.SkuInfoVo;
import com.example.kedamall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    private final String CART_PREFIX="kedamall:cart:";

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        //获取操作
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String res = (String) cartOps.get(skuId.toString());

        if(StringUtils.isEmpty(res)){
            //购物车无此商品
            //添加新商品到购物车
            CartItemVo cartItemVo = new CartItemVo();
            CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
                //远程查询当前要添加的商品的信息
                R r = productFeignService.getSkuInfo(skuId);
                SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                //商品添加到购物车
                cartItemVo.setCheck(true);
                cartItemVo.setCount(num);
                cartItemVo.setImage(skuInfo.getSkuDefaultImg());
                cartItemVo.setTitle(skuInfo.getSkuTitle());
                cartItemVo.setSkuId(skuId);
                cartItemVo.setPrice(skuInfo.getPrice());
            },executor);

            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                //查出sku组合信息
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItemVo.setSkuAttrValues(skuSaleAttrValues);
            }, executor);

            CompletableFuture.allOf(getSkuInfo,getSkuSaleAttrValues).get();
            String s = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(),s);
            return cartItemVo;
        }else {
            CartItemVo cartItemVo = JSON.parseObject(res, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount()+num);
            cartOps.put(skuId.toString(),JSON.toJSONString(cartItemVo));
            return cartItemVo;
        }
    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(res, CartItemVo.class);
        return cartItemVo;
    }

    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        //区分登录与否
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        CartVo cart = new CartVo();
        if(userInfoTo.getUserId()!=null){
            //1.登录了
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
            //2.如果临时购物车的数据还未合并
            String tmpCartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItemVo> tempCartItems = getCartItemByCartKey(tmpCartKey);
            if(tempCartItems!=null && tempCartItems.size()>0){
                //需要合并
                for (CartItemVo tempCartItem : tempCartItems) {
                    addToCart(tempCartItem.getSkuId(),tempCartItem.getCount());
                }
                clearCart(tmpCartKey);
            }

            List<CartItemVo> cartItemByCartKey = getCartItemByCartKey(cartKey);
            cart.setItems(cartItemByCartKey);
        }else {
            //2.没登录
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItemVo> cartItemVos = getCartItemByCartKey(cartKey);
            cart.setItems(cartItemVos);
        }
        return cart;
    }

    private List<CartItemVo> getCartItemByCartKey(String cartKey){
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if(values!=null && values.size()>0){
            List<CartItemVo> cartItemVos = values.stream().map(obj -> {
                String str = (String) obj;
                return JSON.parseObject(str, CartItemVo.class);
            }).collect(Collectors.toList());
            return cartItemVos;
        }
        return null;
    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        //1.得到当前用户信息，决定用哪个购物车
        String cartKey = "";
        if(userInfoTo.getUserId()!=null){
            cartKey = CART_PREFIX+userInfoTo.getUserId();
        }else{
            cartKey = CART_PREFIX+userInfoTo.getUserKey();
        }
        //2.
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    @Override
    public void clearCart(String cartKey){
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),s);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }
}

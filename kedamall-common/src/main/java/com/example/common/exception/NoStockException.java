package com.example.common.exception;

import lombok.Getter;
import lombok.Setter;

public class NoStockException extends RuntimeException {

    @Getter@Setter
    private Long skuId;

    public NoStockException(Long skuId){
        super("商品" + skuId + "没有足够的库存");
    }

    public NoStockException(String msg) {
        super(msg);
    }
}

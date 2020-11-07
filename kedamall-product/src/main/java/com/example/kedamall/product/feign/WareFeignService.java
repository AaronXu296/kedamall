package com.example.kedamall.product.feign;

import com.example.common.to.SkuHasStockVo;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("kedamall-ware")
public interface WareFeignService {

    @PostMapping(value = "/ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}

package com.example.kedamall.seckill.config;

import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.WebFluxCallbackManager;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeckillSentinelConfig {
//        public SeckillSentinelConfig() {
//        WebCallbackManager.setUrlBlockHandler((request, response, ex) -> {
//            R error = R.error(BizCodeEnum.TOO_MANY_REQUESTS_EXCEPTION.getCode(), BizCodeEnum.TOO_MANY_REQUESTS_EXCEPTION.getMsg());
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("application/json");
//            response.getWriter().write(JSON.toJSONString(error));
//        });
//    }
}

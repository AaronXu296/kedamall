package com.example.kedamall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class KedaFeignConfig {

    /**
     * 每次远程调用，都会触发拦截器
     * @return
     */
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //拦截器和原生请求都在同一个线程
                //RequestContextHolder拿到刚进来的请求
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = requestAttributes.getRequest();
                //同步请求头信息
                if(request!=null){
                    requestTemplate.header("Cookie",request.getHeader("Cookie"));
                    System.out.println("feign之前进行的requestInterceptor");
                }
            }
        };
    }
}

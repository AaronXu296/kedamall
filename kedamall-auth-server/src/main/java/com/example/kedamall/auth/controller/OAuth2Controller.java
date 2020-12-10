package com.example.kedamall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.common.constant.AuthServerConstant;
import com.example.common.utils.HttpUtils;
import com.example.common.utils.R;
import com.example.kedamall.auth.feign.MemberFeignService;
import com.example.common.vo.MemberResponseVo;
import com.example.kedamall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class OAuth2Controller {

    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        //1.根据code换取Access Token
        Map<String, String> map = new HashMap<>();
        map.put("client_id","769814124");
        map.put("client_secret","9032bf68eb2ae0c7400a2814c3d4747f");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.kedamall.com/oauth2.0/weibo/success");
        map.put("code",code);
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com",
                "/oauth2/access_token",
                "post",
                new HashMap<String, String>(),
                map,
                new HashMap<String, String>());
        //2.进一步处理
        StatusLine statusLine = response.getStatusLine();
        if(statusLine.getStatusCode()==200){
            //获取到Access Token
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            R r = memberFeignService.oauthLogin(socialUser);
            if(r.getCode()==0){
                MemberResponseVo responseVo = r.getData("data", new TypeReference<MemberResponseVo>() {
                });
                log.info("登录成功，用户信息："+responseVo.toString());
                session.setAttribute(AuthServerConstant.LOGIN_USER,responseVo);
                //3.登陆成功，跳回首页
                return "redirect:http://kedamall.com";
            }else {
                return "redirect:http://auth.kedamall.com/login.html";
            }
        }else{
            return "redirect:http://auth.kedamall.com/login.html";
        }
    }
}

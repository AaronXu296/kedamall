package com.example.kedamall.auth.feign;

import com.example.common.utils.R;
import com.example.kedamall.auth.vo.SocialUser;
import com.example.kedamall.auth.vo.UserLoginVo;
import com.example.kedamall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("kedamall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/regist")
    public R regist(@RequestBody UserRegistVo vo);

    @PostMapping("/member/member/login")
    public R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser vo) throws Exception;
}

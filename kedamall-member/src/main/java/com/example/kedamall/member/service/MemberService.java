package com.example.kedamall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.kedamall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author AaronXu296
 * @email xzy2967@163.com
 * @date 2020-08-02 14:40:15
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


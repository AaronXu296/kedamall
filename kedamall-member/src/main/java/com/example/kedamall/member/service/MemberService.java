package com.example.kedamall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.kedamall.member.entity.MemberEntity;
import com.example.kedamall.member.exception.PhoneExistException;
import com.example.kedamall.member.exception.UserNameExistException;
import com.example.kedamall.member.vo.MemberLoginVo;
import com.example.kedamall.member.vo.MemberRegistVo;
import com.example.kedamall.member.vo.SocialUser;

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

    void regist(MemberRegistVo vo);

    void checkPhoneUnique(String phone) throws PhoneExistException;

    void checkUserNameUnique(String userName) throws UserNameExistException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser vo) throws Exception;
}


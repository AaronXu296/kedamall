package com.example.kedamall.member.dao;

import com.example.kedamall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author AaronXu296
 * @email xzy2967@163.com
 * @date 2020-08-02 14:40:15
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}

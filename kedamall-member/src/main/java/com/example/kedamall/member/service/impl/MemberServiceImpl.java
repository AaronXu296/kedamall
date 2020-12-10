package com.example.kedamall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.common.utils.HttpUtils;
import com.example.kedamall.member.dao.MemberLevelDao;
import com.example.kedamall.member.entity.MemberLevelEntity;
import com.example.kedamall.member.exception.PhoneExistException;
import com.example.kedamall.member.exception.UserNameExistException;
import com.example.kedamall.member.vo.MemberLoginVo;
import com.example.kedamall.member.vo.MemberRegistVo;
import com.example.kedamall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.kedamall.member.dao.MemberDao;
import com.example.kedamall.member.entity.MemberEntity;
import com.example.kedamall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberEntity memberEntity = new MemberEntity();

        //设置默认等级
        MemberLevelEntity level = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(level.getId());

        //检查phone、name唯一性
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());

        //密码要进行加密存储
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);

        //设置UserName
        memberEntity.setUsername(vo.getUserName());
        memberEntity.setNickname(vo.getUserName());

        //其他默认信息的设置


        MemberDao memberDao = this.baseMapper;
        memberDao.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException{
        MemberDao memberDao = this.baseMapper;
        Integer mobile = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(mobile>0){
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUserNameUnique(String userName) throws UserNameExistException {
        MemberDao memberDao = this.baseMapper;
        Integer username = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if(username>0){
            throw new UserNameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {

        String account = vo.getLoginAccount();
        String password = vo.getPassword();

        //去数据集查询盐值
        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().
                eq("username", account).
                or().eq("mobile", account));
        if(memberEntity==null){
            return null;
        }else {
            String passwordDB = memberEntity.getPassword();
            //密码匹配
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean matches = encoder.matches(password, passwordDB);
            if(matches){
                return memberEntity;
            }else {
                return null;
            }
        }
    }

    @Override
    public MemberEntity login(SocialUser vo) throws Exception {
        //注册登录二合一
        String uid = vo.getUid();
        MemberDao memberDao = this.baseMapper;

        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if(memberEntity!=null){
            //此用户已注册
            MemberEntity update = new MemberEntity();
            update.setSocialUid(memberEntity.getSocialUid());
            update.setAccessToken(vo.getAccess_token());
            update.setExpiresIn(vo.getExpires_in());

            memberDao.updateById(update);

            memberEntity.setAccessToken(vo.getAccess_token());
            memberEntity.setExpiresIn(vo.getExpires_in());
            return memberEntity;
        }else {
            //没查到记录，进行注册
            MemberEntity regist = new MemberEntity();
            //查询当前社交用户的信息（性别、昵称等等）
            try {
                Map<String, String> map = new HashMap<>();
                map.put("access_token",vo.getAccess_token());
                map.put("uid",vo.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com",
                        "/2/users/show.json", "get",
                        new HashMap<String, String>(), map);
                if (response.getStatusLine().getStatusCode()==200){
                    //success
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    //.........
                    regist.setNickname(name);
                    regist.setGender(gender.equals("m")?1:0);
                }
            }catch (Exception e){}

                regist.setSocialUid(vo.getUid());
                regist.setAccessToken(vo.getAccess_token());
                regist.setExpiresIn(vo.getExpires_in());

                memberDao.insert(regist);
                return regist;
            }
        }
}

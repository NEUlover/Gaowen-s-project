package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
public class UserServiceimpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    WeChatProperties weChatProperties;

    public static  final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";


    @Override
    public User WXlogin(UserLoginDTO userLoginDTO) {

        //查表
        Map<String,String> claims=new HashMap<>();
        claims.put("appid",weChatProperties.getAppid());
        claims.put("secret",weChatProperties.getSecret());
        claims.put("js_code",userLoginDTO.getCode());
        claims.put("grant_type","authorization_code");
        //向微信官方接口发送请求
        String json = HttpClientUtil.doGet(WX_LOGIN, claims);//拿到了对应账号的json数据
        JSONObject jsonObject = JSON.parseObject(json);//转换成json对象
        String openid = jsonObject.getString("openid");
        if(openid==null)
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);

        //查表
         User user=userMapper.getbyopenid(openid);



        //没有的话注册
        if(user==null)  //如果写set那么就不对,因为空的没法设置
        {  user=User.builder()
                .openid(openid)
                .createTime(LocalDateTime.now())
                .build();
            userMapper.insert(user);
        }
        return user;
    }
}

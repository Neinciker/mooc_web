package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;



    @Override
    public ServiceResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount ==0){
            return   ServiceResponse.createByErrorMessage("用户名不存在");
        }


        // todo md5加密

        String MD5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,MD5password);
        if (user == null){
            return ServiceResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess("登陆成功",user);

    }


    public ServiceResponse<String> register(User user){
        int resultcount = userMapper.checkUsername(user.getUsername());
        if (resultcount>0){
            return ServiceResponse.createByErrorMessage("用户已存在");
        }

        resultcount = userMapper.checkEmail(user.getEmail());
        if (resultcount>0){
            return ServiceResponse.createByErrorMessage("邮箱已存在");
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        resultcount = userMapper.insert(user);
        if (resultcount == 0){
            return ServiceResponse.createByErrorMessage("注册失败");

        }

        return ServiceResponse.createBySuccessMessage("注册成功");



    }



}

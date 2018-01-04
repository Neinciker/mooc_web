package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;


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


        String MD5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,MD5password);
        if (user == null){
            return ServiceResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess("登陆成功",user);

    }


    public ServiceResponse<String> register(User user){

        ServiceResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);

        if (!validResponse.isSuccess()){
            return validResponse;
        }


        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!validResponse.isSuccess()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultcount = userMapper.insert(user);
        if (resultcount == 0){
            return ServiceResponse.createByErrorMessage("注册失败");

        }

        return ServiceResponse.createBySuccessMessage("注册成功");



    }


    public ServiceResponse<String> checkValid(String str ,String type){
        if(StringUtils.isNoneBlank(type)){
            if (Const.USERNAME.equals(type)){
                int resultcount = userMapper.checkUsername(str);
                if (resultcount>0){
                    return ServiceResponse.createByErrorMessage("用户已存在");
                }
            }

            if (Const.EMAIL.equals(type)){
                int resultcount = userMapper.checkUsername(str);
                if (resultcount>0){
                    return ServiceResponse.createByErrorMessage("用户已存在");
                }
            }



        }else {
            return ServiceResponse.createByErrorMessage("参数错误");
        }

        return ServiceResponse.createBySuccessMessage("注册成功");
    }



    public ServiceResponse<String> selectQuestion(String username ){
        ServiceResponse serviceResponse = this.checkValid(username,Const.USERNAME);
        if (serviceResponse.isSuccess()){
            return ServiceResponse.createByErrorMessage("用户的信息不存在");
        }
        String string = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNoneBlank(string)){
            return ServiceResponse.createBySuccess(string);
        }
        return ServiceResponse.createByErrorMessage("找回问题不存在");

    }


    public ServiceResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServiceResponse.createBySuccess(forgetToken);
        }
        return ServiceResponse.createByErrorMessage("回答错误");
    }


    public ServiceResponse<String> forgetResetPassword(String username ,String password ,String forgetToken){
        if (StringUtils.isBlank(forgetToken)){
            return ServiceResponse.createByErrorMessage("token参数错误");
        }
        ServiceResponse serviceResponse = this.checkValid(username,Const.USERNAME);
        if (serviceResponse.isSuccess()){
            return ServiceResponse.createByErrorMessage("用户的信息不存在");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if (StringUtils.isBlank(token)){
            return ServiceResponse.createByErrorMessage("token无效或者过期");
        }

        if (StringUtils.equals(forgetToken,token)){
            String MD5Password = MD5Util.MD5EncodeUtf8(password);
            int rowCount = userMapper.updatePasswordByUsername(username,MD5Password);

            if (rowCount>0){
                return ServiceResponse.createBySuccess("修改密码成功");

            }


        } else{
            return ServiceResponse.createByErrorMessage("token获取错误，请重新获取");
        }
        return ServiceResponse.createByErrorMessage("修改密码失败");

    }


    public ServiceResponse<String> resetPassword( String passwordOld, String passwordNew,User user){
        int count = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if (count== 0){
            return ServiceResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount  = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount>0){
            return ServiceResponse.createBySuccessMessage("密码更新成功");

        }
        return ServiceResponse.createByErrorMessage("密码更新失败");

    }

    public ServiceResponse<User>  updateInfomation(User user){
        int count = userMapper.checkEnmailNotExist(user.getEmail(),user.getId());
        if (count>0){
            return  ServiceResponse.createByErrorMessage("邮箱已存在");
        }
        User upadateUser = new User();
        upadateUser.setId(user.getId());
        upadateUser.setEmail(user.getEmail());
        upadateUser.setAnswer(user.getAnswer());
        upadateUser.setQuestion(user.getQuestion());
        upadateUser.setPhone(user.getPhone());

        int updataCount = userMapper.updateByPrimaryKeySelective(upadateUser);
        if (updataCount>0){
            return ServiceResponse.createBySuccessMessage("更新信息成功");
        }
        return ServiceResponse.createByErrorMessage("更新信息失败");
    }



}

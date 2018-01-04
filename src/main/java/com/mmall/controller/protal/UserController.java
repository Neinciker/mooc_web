package com.mmall.controller.protal;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * login
     * @param username
     * @param password
     * @param session
     * @return
     */

    @RequestMapping( value = "login.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> login(String username, String  password , HttpSession session){
        ServiceResponse<User> serviceResponse = iUserService.login(username,password);

        if (serviceResponse.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,serviceResponse.getData());
        }
        return serviceResponse;
    }

    @RequestMapping( value = "logout.do" , method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<User> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySuccess();
    }

    @RequestMapping( value = "register.do" , method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping( value = "checkValid.do" , method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> checkValid(String str ,String type){
        return iUserService.checkValid(str,type);
    }

    @RequestMapping( value = "getUserInfo.do" , method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServiceResponse.createByErrorMessage("用户未登陆");

        }
        else {
            return ServiceResponse.createBySuccess(user);
        }
    }

    @RequestMapping( value = "forgetGetQuestin.do" , method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> forgetGetQuestin(String username){
        return iUserService.selectQuestion(username);
    }


    @RequestMapping( value = "checkAnswer.do" , method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> checkAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping( value = "forgetResetPassword.do" , method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> forgetResetPassword(String username ,String password ,String forgetToken){
        return iUserService.forgetResetPassword(username, password, forgetToken);
    }

    @RequestMapping( value = "resetPassword.do" , method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServiceResponse.createByErrorMessage("用户未登陆");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    @RequestMapping( value = "updateInfomation.do" , method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<User> updateInfomation(HttpSession session,User user){
        User user1 = (User)session.getAttribute(Const.CURRENT_USER);
        if (user1 == null){
            return ServiceResponse.createByErrorMessage("用户未登陆");
        }
        user.setId(user1.getId());
        user.setUsername(user1.getUsername());
        ServiceResponse<User> userServiceResponse = iUserService.updateInfomation(user);
        if (userServiceResponse.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,userServiceResponse.getData());
        }
        return userServiceResponse;
    }







}

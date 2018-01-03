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
}

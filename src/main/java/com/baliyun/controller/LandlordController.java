package com.baliyun.controller;


import com.baliyun.entity.Landlord;
import com.baliyun.service.LandlordService;
import com.baliyun.utility.MD5Utility;
import com.baliyun.utility.ValidatorUtility;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 吾嘉
 * @since 2019-06-04
 */
@Controller
@RequestMapping("/landlord")
public class LandlordController {

    @Autowired
    private LandlordService landlordService;

    @RequestMapping("/login")    //业主登录
    public String login(Landlord landlord, HttpSession session){

        //调用Validator工具类进行数据校验  --为真则执行用户查询--为假返回登录
        Boolean isValidator = ValidatorUtility.valodator(landlord);

        System.out.println(isValidator);
        if (isValidator.equals(true)){
            //先用MD5给密码进行加密
            MD5Utility md5 = new MD5Utility();
            String lPassword = md5.getMD5(landlord.getlPassword());

            //1.获取Subject
            Subject subject = SecurityUtils.getSubject();
            //2.封装用户数据
            UsernamePasswordToken token = new UsernamePasswordToken(landlord.getlPhone(),lPassword);
            //3.执行登录方法
            try {
                //没有异常登录成功
                subject.login(token);
                //把必要的数据存入session
                List<Landlord> landlordList = landlordService.selectList(new EntityWrapper<Landlord>()
                        .eq("l_phone",landlord.getlPhone())
                        .eq("l_password",lPassword)
                );
                session.setAttribute("lUsername",landlordList.get(0).getlUsername());
                session.setAttribute("lId",landlordList.get(0).getlId());
                return "/landlord/index";
            } catch (UnknownAccountException e) {
                //登录失败:用户名不存在
                e.printStackTrace();
                return "/landlord/login";
            } catch (IncorrectCredentialsException e) {
                //登录失败:密码错误
                e.printStackTrace();
                return "/landlord/login";
            }
        }else {
            return "redirect:/LoginAndRegister/landlordLogin";
        }

    }

    @GetMapping("/internalPages")   //查房源查房间
    public String internalPages(){
        return "";
    }

}


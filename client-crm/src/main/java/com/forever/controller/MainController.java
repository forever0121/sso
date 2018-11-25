package com.forever.controller;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by asus on 2018/11/24.
 */
@Controller
public class MainController {

    @RequestMapping("/main")
    public String main(Model model){
        model.addAttribute("logoutUrl","http://www.sso.com:8443/logout");
        return "main";
    }

    @RequestMapping("logout")
    @ResponseBody
    public String logout(HttpSession httpSession){
        System.out.println(httpSession.getId());
        httpSession.invalidate();
        System.out.println("crm系统注销成功");
        return "success";
    }
}

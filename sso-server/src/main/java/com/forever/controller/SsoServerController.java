package com.forever.controller;

import com.forever.model.UserInfo;
import com.forever.util.MokeDatabaseUtil;
import com.forever.vo.ClientInfoVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by asus on 2018/11/24.
 */
@Controller
public class SsoServerController {

    @RequestMapping("checkLogin")
    public String checkLogin(@RequestParam("redirectUrl") String redirectUrl, HttpSession session, Model model){
        System.out.println(redirectUrl);
        //1 判断是否有全局会话，如果没有，跳转到登录界面
        String token = (String) session.getAttribute("token");
        if (StringUtils.isEmpty(token)){
            //没有全局会话，跳转到登录界面
            model.addAttribute("userInfo",new UserInfo(redirectUrl));
            return "login";
        }else {
            //重定向到redirectUrl,把token带上
            return "redirect:"+redirectUrl+"?token="+token;
        }

    }

    @RequestMapping(value = "login",method = RequestMethod.POST)
    public String login(@ModelAttribute UserInfo userInfo,HttpSession session,Model model){
        if ("admin".equals(userInfo.getUsername()) && "1".equals(userInfo.getPassword())){
            String token = UUID.randomUUID().toString();
            //创建全局会话，将token放入会话中
            session.setAttribute("token",token);
            MokeDatabaseUtil.tokenSet.add(token);
            return "redirect:"+userInfo.getRedirectUrl()+"?token="+token;
        }
        model.addAttribute("redirectUrl",userInfo.getRedirectUrl());
        return "login";
    }

    /**
     * 客户端检查认证中心返回的token是否合法
     * @param token
     * @return
     */
    @RequestMapping("verify")
    @ResponseBody
    public Boolean verify(String token,String clientUrl,String jsessionid){
        System.out.println(token);
        System.out.println(clientUrl);
        System.out.println(jsessionid);//5BCB230FA3B89F96521E082FDAE731CC  7DCA6A8549E8BE8F920C6A29E87B83CC
        if (MokeDatabaseUtil.tokenSet.contains(token)){
            List<ClientInfoVo> list = MokeDatabaseUtil.map.get(token);
            if (MokeDatabaseUtil.map.get(token) == null){
                list = new ArrayList<>();
                ClientInfoVo vo = new ClientInfoVo();
                vo.setClientUrl(clientUrl);
                vo.setJsessionid(jsessionid);
                list.add(vo);
                MokeDatabaseUtil.map.put(token,list);
            }
            ClientInfoVo vo = new ClientInfoVo();
            vo.setClientUrl(clientUrl);
            vo.setJsessionid(jsessionid);
            list.add(vo);
            MokeDatabaseUtil.map.put(token,list);
            return true;
        }else {
            return false;
        }
    }

    @RequestMapping("logout")
    public String loginOut(HttpSession session){
        //销毁全局会话
        session.invalidate();
        return "logout";
    }
}

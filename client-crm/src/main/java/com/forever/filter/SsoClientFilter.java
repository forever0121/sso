package com.forever.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by asus on 2018/11/24.
 */
@Component
@WebFilter(urlPatterns = "/",filterName = "ssoClientFilter")
public class SsoClientFilter implements Filter {

    @Autowired
    private RestTemplate restTemplate;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //1 判断是否有局部会话，如果有，直接放行，如果没有，重定向到认证中心进行认证
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        Boolean isLogin = (Boolean)request.getSession().getAttribute("isLogin");
        if (isLogin!=null&&isLogin){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        //如果参数中含有token，需要向认证中心发起请求，询问该token是否由认证中心颁发
        String token = request.getParameter("token");
        if (token != null && !"".equals(token)){
            ResponseEntity<Boolean> result = restTemplate.getForEntity("http://www.sso.com:8443/verify?token="
                    +token+"&clientUrl="+"http://www.crm.com:8088/logout" +"&jsessionid="+request.getSession().getId(), Boolean.class);
            if (result.getBody()){
                //创建局部会话
                request.getSession().setAttribute("isLogin",true);
                filterChain.doFilter(servletRequest,servletResponse);
                return;
            }
        }
        //重定向到统一认证中心
        String redirectUrl = "http://www.sso.com:8443/checkLogin?redirectUrl="+request.getRequestURL().toString();
        System.out.println(redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}

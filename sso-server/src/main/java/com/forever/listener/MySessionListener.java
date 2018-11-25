package com.forever.listener;

import com.forever.util.MokeDatabaseUtil;
import com.forever.vo.ClientInfoVo;
import org.apache.catalina.SessionEvent;
import org.apache.catalina.SessionListener;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 当调用注销方法或者认证中心session30分钟自动失效时，会触发该监听器
 * Created by asus on 2018/11/25.
 */
@Component
public class MySessionListener implements HttpSessionListener {

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("销毁局部会话监听器----------");
        HttpSession session = se.getSession();
        String token = (String) session.getAttribute("token");
        MokeDatabaseUtil.tokenSet.remove(token);
        //让客户端注销各自的session
        List<ClientInfoVo> list = MokeDatabaseUtil.map.get(token);
        for (ClientInfoVo clientInfoVo : list) {
            try{
                URL url = new URL(clientInfoVo.getClientUrl());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                System.out.println(clientInfoVo.getJsessionid());
                con.addRequestProperty("Cookie","JSESSIONID="+clientInfoVo.getJsessionid());
                con.connect();
                con.getInputStream();
                con.disconnect();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
}

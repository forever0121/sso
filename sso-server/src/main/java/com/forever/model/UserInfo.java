package com.forever.model;

/**
 * Created by asus on 2018/11/24.
 */
public class UserInfo {
    private String redirectUrl;
    private String username;
    private String password;

    public UserInfo(String redirectUrl){
        this.redirectUrl = redirectUrl;
    }
    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

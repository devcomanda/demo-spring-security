package com.devcomanda.demospringsecurity.web.api.requests;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class LoginReq {
    private String email;
    private String password;

    private boolean rememberMe;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}

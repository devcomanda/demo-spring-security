package com.devcomanda.demospringsecurity.web.api.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class NewUserReq extends UpdateUserReq {
    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    @NotBlank
    @Email
    @Size(min = 5, max = 100)
    private String email;

    @Size(min = NewUserReq.PASSWORD_MIN_LENGTH, max = NewUserReq.PASSWORD_MAX_LENGTH)
    private String password;

    public NewUserReq() {
    }

    public NewUserReq(
            @NotBlank @Email @Size(min = 5, max = 100) final String email,
            @Size(min = NewUserReq.PASSWORD_MIN_LENGTH, max = NewUserReq.PASSWORD_MAX_LENGTH) final String password,
            @Size(max = 75) final String firstName,
            @Size(max = 75) final String lastName
    ) {
        super(firstName, lastName);
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "NewUserReq{" +
                "email='" + this.email + '\'' +
                ", first name='" + this.getFirstName() + '\'' +
                ", last name='" + this.getLastName() + '\'' +
                '}';
    }
}

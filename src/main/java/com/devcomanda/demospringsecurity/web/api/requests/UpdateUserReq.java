package com.devcomanda.demospringsecurity.web.api.requests;

import javax.validation.constraints.Size;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class UpdateUserReq {
    @Size(min = 1, max = 75)
    private String firstName;
    @Size(min = 1, max = 75)
    private String lastName;

    public UpdateUserReq() {
    }

    public UpdateUserReq(final String firstName, final String lastName) {

        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }
}

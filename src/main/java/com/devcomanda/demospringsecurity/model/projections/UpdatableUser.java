package com.devcomanda.demospringsecurity.model.projections;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class UpdatableUser {

    @NotNull
    private Long id;

    @Size(min = 1, max = 75)
    private String firstName;

    @Size(min = 1, max = 75)
    private String lastName;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    public UpdatableUser(
            final Long id,
            final String firstName,
            final String lastName,
            final String email
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
}

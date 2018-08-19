package com.devcomanda.demospringsecurity.model.projections;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public interface ReadableUser {
    @Value("#{target.id}")
    Long getId();

    String getEmail();

    String getFirstName();

    String getLastName();

    Boolean getActivated();
}

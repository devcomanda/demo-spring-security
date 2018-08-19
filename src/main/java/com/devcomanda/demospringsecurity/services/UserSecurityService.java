package com.devcomanda.demospringsecurity.services;

import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.web.api.requests.NewUserReq;

import java.time.Instant;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public interface UserSecurityService {

    User register(NewUserReq req);

    User activate(String key);

    User activate(Long userId);

    void removeNotActivatedUsersBefore(Instant before);

    void remindPassword(String email);
}

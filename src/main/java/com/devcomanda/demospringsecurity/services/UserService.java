package com.devcomanda.demospringsecurity.services;

import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.model.projections.ReadableUser;
import com.devcomanda.demospringsecurity.model.projections.UpdatableUser;
import com.devcomanda.demospringsecurity.web.api.requests.NewUserReq;

import java.util.List;
import java.util.Optional;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public interface UserService {

    User createUser(NewUserReq req, boolean isActivate);

    User updateUser(String firstName, String lastName);

    User updateUser(UpdatableUser user);

    void deleteUser(Long userId);

    Optional<UpdatableUser> loadUpdatableUserById(Long userId);

    Optional<ReadableUser> loadUserById(Long userId);

    User loadUserByEmail(String email);

    List<ReadableUser> loadUsersByAuthority(String role);

    void changePassword(User user, String newPassword);

    Optional<ReadableUser> loadReadableByEmail(String email);
}

package com.devcomanda.demospringsecurity.web.api;

import com.devcomanda.demospringsecurity.exceptions.InternalServerException;
import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.model.projections.ReadableUser;
import com.devcomanda.demospringsecurity.services.UserService;
import com.devcomanda.demospringsecurity.utils.SecurityUtils;
import com.devcomanda.demospringsecurity.web.api.requests.UpdateUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RestController
@RequestMapping("/api/account")
public class AccountResource {

    private final UserService userService;

    @Autowired
    public AccountResource(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ReadableUser getCurrentUserAccount() {
        final String email = SecurityUtils.getCurrentUserLogin();
        return this.userService.loadReadableByEmail(email)
                .orElseThrow(() -> new InternalServerException("User could not be found"));
    }

    @PostMapping
    public void updateCurrentUser(final @Valid @RequestBody UpdateUserReq req) {
        final User user = this.userService.updateUser(req.getFirstName(), req.getLastName());

        if (user == null) {
            throw new InternalServerException("We cannot update current user");
        }
    }
}

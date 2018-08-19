package com.devcomanda.demospringsecurity.web.api.security;

import com.devcomanda.demospringsecurity.exceptions.ValidationNewUserReqException;
import com.devcomanda.demospringsecurity.services.UserSecurityService;
import com.devcomanda.demospringsecurity.web.api.requests.NewUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RestController
@RequestMapping("/api/security")
public class UserSecurityResource {

    private final UserSecurityService securityService;

    @Autowired
    public UserSecurityResource(
            final UserSecurityService securityService
    ) {
        this.securityService = securityService;
    }

    @PostMapping("public/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(
            @Valid @RequestBody final NewUserReq req,
            final BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            final String errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::toString)
                    .collect(Collectors.joining(","));

            throw new ValidationNewUserReqException(errors);
        }

        this.securityService.register(req);
    }

    @GetMapping(path = "public/remind-password")
    public void remindPassword(@RequestParam("email") final String email) {
        this.securityService.remindPassword(email);
    }
}

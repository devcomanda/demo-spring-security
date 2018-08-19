package com.devcomanda.demospringsecurity.web.ui.security;

import com.devcomanda.demospringsecurity.services.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Controller
public class UserSecurityController {

    private final UserSecurityService securityService;

    @Autowired
    public UserSecurityController(final UserSecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/security/activate/{key}")
    public String displayResultUserActivation(
            @PathVariable("key") final String key
    ) {

        try {
            this.securityService.activate(key);
            return "security/successfulActivation";
        } catch (RuntimeException e) {
            return "security/errorActivation";
        }
    }

}

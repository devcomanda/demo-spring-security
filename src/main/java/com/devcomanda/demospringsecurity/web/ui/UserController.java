package com.devcomanda.demospringsecurity.web.ui;

import com.devcomanda.demospringsecurity.exceptions.InternalServerException;
import com.devcomanda.demospringsecurity.exceptions.security.UserNotFoundException;
import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.model.projections.ReadableUser;
import com.devcomanda.demospringsecurity.model.projections.UpdatableUser;
import com.devcomanda.demospringsecurity.services.UserSecurityService;
import com.devcomanda.demospringsecurity.services.UserService;
import com.devcomanda.demospringsecurity.web.api.requests.NewUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Locale;

import static com.devcomanda.demospringsecurity.config.constants.AuthoritiesConstants.ROLE_USER;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Controller
@RequestMapping("/admin")
public class UserController {

    private final UserService userService;

    private final UserSecurityService securityService;

    @Autowired
    public UserController(
            final UserService userService,
            final UserSecurityService securityService
    ) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @GetMapping("/users")
    public String displayListUsers(
            @RequestParam(value = "authority", defaultValue = ROLE_USER, required = false) final String authority,
            final Model model
    ) {
        model.addAttribute("users",
                this.userService.loadUsersByAuthority(authority.toUpperCase(Locale.ENGLISH))
        );
        return "users/listUsers";
    }

    @GetMapping("/users/new")
    public String displayFormCreateUser(final Model model) {
        model.addAttribute("newUser", new NewUserReq());
        return "users/formCreateUser";
    }

    @PostMapping("/users/new")
    public String processFormCreateUser(
            final Model model,
            final @Valid @ModelAttribute("newUser") NewUserReq req,
            final BindingResult bindingResult

    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("newUser", req);
            return "users/formCreateUser";
        }

        final boolean isActivate = true;
        final User user = this.userService.createUser(req, isActivate);

        if (user == null) {
            throw new InternalServerException("We cannot create user");
        }

        return "redirect:/admin/users/" + user.getId();
    }

    @GetMapping("/users/{userId}")
    public String displayUserDetails(
            final @PathVariable("userId") Long userId,
            final Model model

    ) {
        final ReadableUser user = this.userService.loadUserById(userId)
                .orElseThrow(UserNotFoundException::new);
        model.addAttribute("user", user);
        return "users/userDetails";
    }

    @GetMapping("/users/{userId}/update")
    public String displayFormUpdateUser(
            final @PathVariable Long userId,
            final Model model
    ) {
        final UpdatableUser user =
                this.userService.loadUpdatableUserById(userId)
                        .orElseThrow(UserNotFoundException::new);

        model.addAttribute("user", user);
        return "users/formUpdateUser";
    }

    @PostMapping("/users/*/update")
    public String processFormUpdateUser(
            final Model model,
            final @Valid @ModelAttribute("user") UpdatableUser updatedUser,
            final BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", updatedUser);
            return "users/formUpdateUser";
        }

        final User user = this.userService.updateUser(updatedUser);

        if (user == null) {
            throw new InternalServerException("We cannot update user");
        }
        return "redirect:/admin/users/" + user.getId();
    }

    @PostMapping("/users/{userId}/delete")
    public String processFormDeleteUser(@PathVariable("userId") final Long userId) {
        this.userService.deleteUser(userId);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{userId}/activate")
    public String processFormActivateUser(@PathVariable("userId") final Long userId) {
        this.securityService.activate(userId);
        return "redirect:/admin/users/" + userId;
    }
}

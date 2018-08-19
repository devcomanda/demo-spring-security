package com.devcomanda.demospringsecurity.security;


import com.devcomanda.demospringsecurity.exceptions.security.UserNotActivatedException;
import com.devcomanda.demospringsecurity.model.User;
import com.devcomanda.demospringsecurity.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    @Autowired
    public DomainUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) {
        this.log.debug("Authenticating {}", email);
        final String lowercaseEmail = email.toLowerCase(Locale.ENGLISH);
        final Optional<User> userFromDatabase = this.userRepository.findOneWithAuthoritiesByEmailIgnoreCase(lowercaseEmail);
        return userFromDatabase.map(user -> {
            if (!user.isActivated()) {
                throw new UserNotActivatedException("User " + lowercaseEmail + " was not activated");
            }
            List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                    .collect(Collectors.toList());
            return new org.springframework.security.core.userdetails.User(lowercaseEmail,
                    user.getPassword(),
                    grantedAuthorities);
        }).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseEmail + " was not found in the " +
                "database"));
    }
}

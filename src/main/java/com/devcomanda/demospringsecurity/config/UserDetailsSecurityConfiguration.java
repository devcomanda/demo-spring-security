package com.devcomanda.demospringsecurity.config;

import com.devcomanda.demospringsecurity.repositories.UserRepository;
import com.devcomanda.demospringsecurity.security.DomainUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Configuration
public class UserDetailsSecurityConfiguration {

// simple in-memory user details configuration
//    @Bean
//    public UserDetailsService userDetailsService() throws Exception {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withUsername("root@root.com").password("{noop}root")
//                .roles("ADMIN").build());
//        return manager;
//    }


    // Our custom user details service configuration

    @Autowired
    private UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return new DomainUserDetailsService(userRepository);
    }
}

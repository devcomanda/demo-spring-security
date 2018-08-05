package com.devcomanda.demospringsecurity.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                //needs to be the public
                .antMatchers("/styles/**").permitAll()
                .antMatchers("/public/**").permitAll()
                .antMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/private/demomodels")
                //needs to be the public
                .permitAll();
    }
}

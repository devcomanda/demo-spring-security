package com.devcomanda.demospringsecurity.config;

import com.devcomanda.demospringsecurity.security.JWTConfigurer;
import com.devcomanda.demospringsecurity.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@EnableWebSecurity
public class SecurityConfiguration {


    private final UserDetailsService userDetailsService;

    private final TokenProvider tokenProvider;

    @Autowired
    public SecurityConfiguration(
            final UserDetailsService userDetailsService,
            final TokenProvider tokenProvider
    ) {

        this.userDetailsService = userDetailsService;
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    @Order(1)
    public class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .userDetailsService(userDetailsService)
                    .antMatcher("/api/**")
                    .csrf()
                    .disable()
                    .headers()
                    .frameOptions()
                    .disable()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/api/public/**").permitAll()
                    .antMatchers("/api/security/public/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .apply(this.securityConfigurerAdapter());

        }

        private JWTConfigurer securityConfigurerAdapter() {
            return new JWTConfigurer(SecurityConfiguration.this.tokenProvider);
        }

        @Bean(name = "apiAuthManager")
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

    }

    @Configuration
    public class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .userDetailsService(userDetailsService)
                    .authorizeRequests()
                    .antMatchers("/public/**").permitAll()
                    .antMatchers("/security/**").permitAll()
                    .antMatchers("/styles/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .usernameParameter("email")
                    .defaultSuccessUrl("/")
                    .loginPage("/login")
                    .permitAll()
                    .and()
                    .logout()
                    .logoutSuccessUrl("/login")
                    .deleteCookies("JSESSIONID")
                    .permitAll()
                    .and()
                    .exceptionHandling();
        }
    }
}

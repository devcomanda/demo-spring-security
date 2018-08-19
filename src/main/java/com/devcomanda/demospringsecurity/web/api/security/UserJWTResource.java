package com.devcomanda.demospringsecurity.web.api.security;

import com.devcomanda.demospringsecurity.security.JWTConfigurer;
import com.devcomanda.demospringsecurity.security.TokenProvider;
import com.devcomanda.demospringsecurity.web.api.requests.LoginReq;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@RestController
@RequestMapping("/api/security/public")
public class UserJWTResource {

    private final TokenProvider tokenProvider;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserJWTResource(
            final TokenProvider tokenProvider,
            @Qualifier("apiAuthManager") final AuthenticationManager authenticationManager
    ) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(final @RequestBody LoginReq loginReq) {

        final UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginReq.getEmail(), loginReq.getPassword());

        final Authentication authentication = this.authenticationManager.authenticate(authToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final boolean rememberMe = loginReq.isRememberMe();

        final String jwt = this.tokenProvider.createToken(authentication, rememberMe);
        final HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new UserJWTResource.JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/authenticate")
    public String isAuthenticated(final HttpServletRequest request) {
        return request.getRemoteUser();
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    private static class JWTToken {

        private String idToken;

        JWTToken(final String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return this.idToken;
        }

        void setIdToken(final String idToken) {
            this.idToken = idToken;
        }
    }
}

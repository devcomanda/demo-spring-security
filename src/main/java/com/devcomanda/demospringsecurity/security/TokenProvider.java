package com.devcomanda.demospringsecurity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Component("tokenProvider")
public class TokenProvider {
    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.tokenValidityInMilliseconds}")
    private long tokenValidityInMilliseconds;

    @Value("${jwt.tokenValidityInMillisecondsForRememberMe}")
    private long tokenValidityInMillisecondsForRememberMe;

    public String createToken(final Authentication authentication, final Boolean rememberMe) {

        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        final long now = new Date().getTime();
        final Date validity;

        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(TokenProvider.AUTHORITIES_KEY, authorities)
                .signWith(SignatureAlgorithm.HS512, this.secretKey)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(final String token) {
        final Claims claims = Jwts.parser()
                .setSigningKey(this.secretKey)
                .parseClaimsJws(token)
                .getBody();

        final Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(TokenProvider.AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        final User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(final String authToken) {
        try {
            Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            this.log.info("Invalid JWT signature.");
            this.log.trace("Invalid JWT signature trace: {}", e);
        } catch (MalformedJwtException e) {
            this.log.info("Invalid JWT token.");
            this.log.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {
            this.log.info("Expired JWT token.");
            this.log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            this.log.info("Unsupported JWT token.");
            this.log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            this.log.info("JWT token compact of handler are invalid.");
            this.log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }
}

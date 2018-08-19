package com.devcomanda.demospringsecurity.security.jwt;

import com.devcomanda.demospringsecurity.security.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class TokenProviderTest {
    private static final String SECRET_KEY = "e5c9ee274ae87bc031adda32e27fa98b9290da83";
    private static final long ONE_MINUTE = 60000L;
    private TokenProvider tokenProvider;

    @Before
    public void setup() {
        this.tokenProvider = new TokenProvider();
        ReflectionTestUtils.setField(this.tokenProvider, "secretKey", TokenProviderTest.SECRET_KEY);
        ReflectionTestUtils.setField(this.tokenProvider, "tokenValidityInMilliseconds", TokenProviderTest.ONE_MINUTE);
    }

    @Test
    public void shouldReturnFalseWhenJWThasInvalidSignature() {
        final boolean isTokenValid = this.tokenProvider.validateToken(this.createTokenWithDifferentSignature());

        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void shouldReturnFalseWhenJWTisMalformed() {
        final Authentication authentication = this.createAuthentication();
        final String token = this.tokenProvider.createToken(authentication, false);
        final String invalidToken = token.substring(1);
        final boolean isTokenValid = this.tokenProvider.validateToken(invalidToken);

        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void shouldReturnFalseWhenJWTisExpired() {
        ReflectionTestUtils.setField(this.tokenProvider, "tokenValidityInMilliseconds", -TokenProviderTest.ONE_MINUTE);

        final Authentication authentication = this.createAuthentication();
        final String token = this.tokenProvider.createToken(authentication, false);

        final boolean isTokenValid = this.tokenProvider.validateToken(token);

        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void shouldReturnFalseWhenJWTisUnsupported() {
        final String unsupportedToken = this.createUnsupportedToken();

        final boolean isTokenValid = this.tokenProvider.validateToken(unsupportedToken);

        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void shouldReturnFalseWhenJWTisInvalid() {
        final boolean isTokenValid = this.tokenProvider.validateToken("");

        assertThat(isTokenValid).isEqualTo(false);
    }

    private Authentication createAuthentication() {
        final Collection<GrantedAuthority> authorities = new ArrayList<>(1);
        authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        return new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities);
    }

    private String createUnsupportedToken() {
        return Jwts.builder()
                .setPayload("payload")
                .signWith(SignatureAlgorithm.HS512, TokenProviderTest.SECRET_KEY)
                .compact();
    }

    private String createTokenWithDifferentSignature() {
        return Jwts.builder()
                .setSubject("anonymous")
                .signWith(SignatureAlgorithm.HS512, "e5c9ee274ae87bc031adda32e27fa98b9290da90")
                .setExpiration(new Date(new Date().getTime() + TokenProviderTest.ONE_MINUTE))
                .compact();
    }
}
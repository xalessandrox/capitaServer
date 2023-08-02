package com.sandro.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.sandro.domain.UserPrincipal;
import com.sandro.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 02.07.2023
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenProvider {

    private static final String SANDRO_DEV = "SANDRO_DEV";
    private static final String CUSTOMER_MANAGEMENT_SERVICE = "CUSTOMER_MANAGEMENT_SERVICE";
    public static final String AUTHORITIES = "authorities";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME =  86_400_000;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;

    @Value("${jwt.secret}")
    public String secret;
    private final UserService userService;

    public String createAccessToken(UserPrincipal userPrincipal) {

        return JWT.create()
                .withIssuer(SANDRO_DEV)
                .withIssuedAt(new Date())
                .withSubject(String.valueOf(userPrincipal.getUser().getId()))
                .withAudience(CUSTOMER_MANAGEMENT_SERVICE)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .withArrayClaim(AUTHORITIES, getClaimsFromUser(userPrincipal))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public String createRefreshToken(UserPrincipal userPrincipal) {

        return JWT.create()
                .withIssuer(SANDRO_DEV)
                .withAudience(CUSTOMER_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date())
                .withSubject(String.valueOf(userPrincipal.getUser().getId()))
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public Long getSubject(String token, HttpServletRequest request) {

        try {
            return Long.valueOf(getJWTVerifier().verify(token).getSubject());
        }  catch (InvalidClaimException exception) {
            log.error("Claim is invalid");
            request.setAttribute("invalidClaim", exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            log.error("Error occurred with the jwt", exception.getMessage());
            throw exception;
        }

    }

    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {

        return userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }

    // turns claims into authorities
    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public Authentication getAuthentication(Long userId, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userService.getUserById(userId), null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    public boolean isTokenValid(Long userId, String token) {
        JWTVerifier verifier = getJWTVerifier();
        return !Objects.isNull(userId) && !isTokenExpired(verifier, token);
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expirationTime = verifier.verify(token).getExpiresAt();
        return expirationTime.before(new Date());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {

        try {
            Algorithm alg = Algorithm.HMAC512(secret);
            return JWT.require(alg)
                    .withIssuer(SANDRO_DEV)
                    .build();
        } catch (JWTVerificationException exc) {
            log.error("Web Token could not be verified");
            throw exc;
        }
    }

}

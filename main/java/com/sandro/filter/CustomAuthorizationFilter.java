package com.sandro.filter;

import com.sandro.provider.TokenProvider;
import com.sandro.utils.ExceptionUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.sandro.utils.Constants.*;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04.07.2023
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getToken(request);
//            System.out.println("TOKEN:\n" + token);
            Long userId = getUserId(request);
            if (tokenProvider.isTokenValid(userId, token)) {
                List<GrantedAuthority> authorities = tokenProvider.getAuthorities(token);
                Authentication authentication = tokenProvider.getAuthentication(userId, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            ExceptionUtils.processError(request, response, exception);
        }
    }

    private String getToken(HttpServletRequest request) {
        //        return request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Bearer ");
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(h -> h.startsWith(TOKEN_PREFIX))
                .map(token -> token.replace(TOKEN_PREFIX, StringUtils.EMPTY)).get();
    }

    private Long getUserId(HttpServletRequest request) {
        return tokenProvider.getSubject(getToken(request), request);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return
                request.getHeader(HttpHeaders.AUTHORIZATION) == null
                        || !request.getHeader(HttpHeaders.AUTHORIZATION).startsWith(TOKEN_PREFIX)
                        || request.getMethod().equalsIgnoreCase(HTTP_OPTIONS_METHOD)
                        || Arrays.asList(PUBLIC_URLS_CUST_AUTH_FILTER).contains(request.getRequestURI());
    }

}

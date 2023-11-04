package com.sandro.utils;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 16.09.2023
 */


public interface Constants {

    // Web Security Config
    String[] PUBLIC_URLS = {
            "/user/register/**",
            "/user/login/**",
            "/user/verify/code/**",
            "/user/verify/password/**",
            "/user/reset-password/**",
            "/user/verify/account/**",
            "/user/refresh/token/**",
            "/user/image/**",
            "/user/new/password"
    };

    String[] SWAGGER_UI = {
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/webjars/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/proxy/**"
    };

    //Custom Authorization Filter
    String[] PUBLIC_URLS_CUST_AUTH_FILTER = {"/user/register", "/user/login", "/user/verify/code", "/user/refresh/token", "user/image"};
    String HTTP_OPTIONS_METHOD = "OPTIONS";
    String TOKEN_PREFIX = "Bearer ";


    //  Token Provider
    String SANDRO_DEV = "SANDRO_DEV";
    String CUSTOMER_MANAGEMENT_SERVICE = "CUSTOMER_MANAGEMENT_SERVICE";
    String AUTHORITIES = "authorities";
   long ACCESS_TOKEN_EXPIRATION_TIME =  10_800_000; // 3 hours
    long REFRESH_TOKEN_EXPIRATION_TIME = 21_600_000; // 6 hours

}

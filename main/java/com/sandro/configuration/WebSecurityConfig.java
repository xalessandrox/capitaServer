package com.sandro.configuration;

import com.sandro.filter.CustomAuthorizationFilter;
import com.sandro.handler.CustomAccessDeniedHandler;
import com.sandro.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.sandro.utils.Constants.PUBLIC_URLS;
import static com.sandro.utils.Constants.SWAGGER_UI;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 26.06.2023
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder encoder;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAuthorizationFilter customAuthorizationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::getClass)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> {
                    request
                            .requestMatchers(PUBLIC_URLS).permitAll()
                            .requestMatchers(SWAGGER_UI).permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/users/delete/**").hasAnyAuthority("DELETE:USER")
                            .requestMatchers(HttpMethod.DELETE, "/customers/delete/**").hasAnyAuthority("DELETE:CUSTOMER")
                            .requestMatchers(new String[]{"/customers/search**", "/customers/search"}).hasAnyAuthority("READ:OPENAPI")
                            .anyRequest().authenticated();
                })
                .exceptionHandling(exception -> {
                    exception
                            .accessDeniedHandler(customAccessDeniedHandler)
                            .authenticationEntryPoint(customAuthenticationEntryPoint);
                })
                .addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(encoder);
        return new ProviderManager(authenticationProvider);
    }

}

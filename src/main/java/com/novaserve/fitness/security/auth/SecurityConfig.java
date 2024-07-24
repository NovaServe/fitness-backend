/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  @Autowired CustomUserDetails customUserDetails;

  @Autowired CustomAuthProvider customAuthProvider;

  @Autowired JwtAuthEntry jwtAuthEntry;

  @Autowired JwtAuthFilter jwtAuthFilter;

  @Autowired OpenEndpoints openEndpoints;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .exceptionHandling(
            exceptionHandling -> exceptionHandling.authenticationEntryPoint(jwtAuthEntry))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(HttpMethod.GET, openEndpoints.getErrorUrl())
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, openEndpoints.getErrorUrl())
                    .permitAll()
                    .requestMatchers(HttpMethod.PATCH, openEndpoints.getErrorUrl())
                    .permitAll()
                    .requestMatchers(HttpMethod.DELETE, openEndpoints.getErrorUrl())
                    .permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, openEndpoints.getLoginUrl())
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, openEndpoints.getLogoutUrl())
                    .permitAll()
                    .anyRequest()
                    .authenticated());

    // http.authenticationProvider(authenticationProvider());
    http.authenticationProvider(customAuthProvider);
    http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  //    @Bean
  //    public DaoAuthenticationProvider authenticationProvider() {
  //        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
  //        authProvider.setUserDetailsService(customUserDetailsService);
  //        authProvider.setPasswordEncoder(passwordEncoder());
  //        return authProvider;
  //    }
}

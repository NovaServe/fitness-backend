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
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    JWTAuthEntryPoint jwtAuthEntryPoint;

    @Autowired
    JWTAuthFilter jwtAuthFilter;

    @Autowired
    OpenEndpoints openEndpoints;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, openEndpoints.getErrorURL())
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, openEndpoints.getErrorURL())
                        .permitAll()
                        .requestMatchers(HttpMethod.PATCH, openEndpoints.getErrorURL())
                        .permitAll()
                        .requestMatchers(HttpMethod.DELETE, openEndpoints.getErrorURL())
                        .permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, openEndpoints.getLoginURL())
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, openEndpoints.getLogoutURL())
                        .permitAll()
                        .anyRequest()
                        .authenticated());

        //        httpSecurity.authenticationProvider(authenticationProvider());
        httpSecurity.authenticationProvider(customAuthenticationProvider);
        httpSecurity.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    //    @Bean
    //    public DaoAuthenticationProvider authenticationProvider() {
    //        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    //        authProvider.setUserDetailsService(customUserDetailsService);
    //        authProvider.setPasswordEncoder(passwordEncoder());
    //        return authProvider;
    //    }
}

package com.skyline.skatecross.common.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.skyline.skatecross.common.security.ApiSecurityErrorHandler;
import com.skyline.skatecross.common.security.JwtAuthenticationFilter;
import com.skyline.skatecross.common.security.JwtService;
import com.skyline.skatecross.modules.auth.service.AuthService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        JwtService jwtService,
                        AuthService authService,
                        ApiSecurityErrorHandler errorHandler) throws Exception {

                http
                                .cors()
                                .and()
                                .csrf().disable()
                                .httpBasic().disable()
                                .formLogin().disable()
                                .sessionManagement()
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                .and()
                                .exceptionHandling()
                                .authenticationEntryPoint(errorHandler)
                                .accessDeniedHandler(errorHandler)
                                .and()
                                .authorizeRequests()

                                // Preflight request
                                .antMatchers(
                                                HttpMethod.OPTIONS,
                                                "/**")
                                .permitAll()

                                // Login publik
                                .antMatchers(
                                                HttpMethod.POST,
                                                "/api/auth/login")
                                .permitAll()

                                // Swagger dan health
                                .antMatchers(
                                                "/error",
                                                "/swagger-ui.html",
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**",
                                                "/api/health",
                                                "/actuator/health")
                                .permitAll()

                                // Hanya Super Admin
                                .antMatchers(
                                                "/api/users",
                                                "/api/users/**",
                                                "/api/roles",
                                                "/api/roles/**")
                                .hasRole("SUPER_ADMIN")

                                // Endpoint lainnya wajib login
                                .anyRequest().authenticated();

                http.addFilterBefore(
                                new JwtAuthenticationFilter(
                                                jwtService,
                                                authService),
                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource(
                        @Value("${app.cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173}") String origins) {
                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOrigins(
                                Arrays.asList(origins.split("\\s*,\\s*")));

                config.setAllowedMethods(
                                Arrays.asList(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "PATCH",
                                                "DELETE",
                                                "OPTIONS"));

                config.setAllowedHeaders(
                                Arrays.asList(
                                                "Authorization",
                                                "Content-Type"));

                config.setAllowCredentials(false);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                config);

                return source;
        }
}
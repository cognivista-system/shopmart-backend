package com.shopmart.config;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final ObjectMapper objectMapper;

    private static final String[] PUBLIC_PATHS = {
            "/auth/register", "/auth/login", "/auth/google", "/auth/refresh-token",
            "/auth/verify-otp", "/auth/resend-otp",
            "/auth/forgot-password", "/auth/reset-password",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
            "/actuator/health",
            "/products/*/view"
    };

    private static final String[] PUBLIC_GET_PATHS = {
            "/products/**", "/categories/**", "/brands/**",
            "/blogs", "/blogs/*",   // published list + single post by slug (admin sub-paths stay protected)
            "/search", "/search/suggest", "/search/trending",   // history stays protected
            "/recommendations/trending",
            "/vendors/store/*",       // public storefront by slug
            "/banners",               // active banners only; /banners/all and /banners/{id} stay admin
            "/currencies", "/currencies/convert",   // currency table + conversion
            "/translations",          // localized fields lookup (admin upsert/delete stay protected)
            "/sitemap.xml", "/robots.txt", "/seo/**",  // SEO
            "/mobile/config"          // mobile app bootstrap config
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_PATHS).permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, PUBLIC_GET_PATHS).permitAll()
                // Super Admin only
                .requestMatchers("/super-admin/**").hasRole("SUPER_ADMIN")
                // Admin area — Admin or Super Admin
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getWriter(),
                        ApiResponse.error("Authentication required"));
            }))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

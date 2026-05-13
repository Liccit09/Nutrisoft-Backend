package com.nutrisoft.userinterface.api.rest.config;

import com.nutrisoft.userinterface.api.rest.auth.security.CustomUserDetailsService;
import com.nutrisoft.userinterface.api.rest.auth.security.JwtAuthenticationFilter;
import com.nutrisoft.userinterface.api.rest.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Spring Security Configuration.
 * Configures authentication, authorization, and security filters.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    // Role constants
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_PATIENT = "PATIENT";
    private static final String ROLE_PROFESSIONAL = "PROFESSIONAL";

    // Endpoint constants - Routes only (without HTTP methods)
    private static final String ENDPOINT_AUTH_LOGIN = "/v1/auth/login";
    private static final String ENDPOINT_AUTH_REFRESH_TOKEN = "/v1/auth/refresh-token";
    private static final String ENDPOINT_AUTH_LOGOUT = "/v1/auth/logout";
    private static final String ENDPOINT_PATIENT_REGISTER = "/v1/patients/register";
    private static final String ENDPOINT_PROFESSIONALS = "/v1/professionals";
    private static final String ENDPOINT_PROFESSIONAL_REGISTER = "/v1/professionals/register";
    private static final String ENDPOINT_APPOINTMENTS = "/v1/appointments";
    private static final String ENDPOINT_APPOINTMENTS_ID = "/v1/appointments/{appointmentId}";
    private static final String ENDPOINT_APPOINTMENTS_CONFIRM = "/v1/appointments/{appointmentId}/confirm";
    private static final String ENDPOINT_APPOINTMENTS_CANCEL = "/v1/appointments/{appointmentId}/cancel";
    private static final String ENDPOINT_APPOINTMENTS_COMPLETE = "/v1/appointments/{appointmentId}/complete";
    private static final String ENDPOINT_APPOINTMENTS_NO_SHOW = "/v1/appointments/{appointmentId}/no-show";
    private static final String ENDPOINT_APPOINTMENTS_RESCHEDULE = "/v1/appointments/{appointmentId}/reschedule";
    private static final String ENDPOINT_APPOINTMENTS_VIRTUAL_LINK = "/v1/appointments/{appointmentId}/virtual-link";
    private static final String ENDPOINT_APPOINTMENTS_BY_PATIENT = "/v1/appointments/patient/{patientId}";
    private static final String ENDPOINT_APPOINTMENTS_BY_PROFESSIONAL = "/v1/appointments/professional/{professionalId}";
    private static final String ENDPOINT_AVAILABILITY_DATES = "/v1/appointments/availability/dates";
    private static final String ENDPOINT_AVAILABILITY_TIME_SLOTS = "/v1/appointments/availability/time-slots";
    private static final String ENDPOINT_PROFILE = "/v1/profile";
    private static final String ENDPOINT_ADMIN = "/v1/admin/**";
    private static final String ENDPOINT_USERS = "/v1/users/**";
    private static final String ENDPOINT_SERVICES = "/v1/services";
    
    // Swagger/Documentation routes
    private static final String ENDPOINT_SWAGGER_UI = "/swagger-ui/**";
    private static final String ENDPOINT_SWAGGER_CONFIG = "/swagger-ui.html";
    private static final String ENDPOINT_API_DOCS = "/v1/api-docs/**";
    private static final String ENDPOINT_API_DOCS_YAML = "/v1/api-docs.yaml";
    private static final String ENDPOINT_WEBJARS = "/webjars/**";
    
    // Health and monitoring
    private static final String ENDPOINT_HEALTH = "/actuator/health";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                    // ========== PUBLIC ENDPOINTS (No authentication required) ==========
                    // Auth API - Login
                    .requestMatchers("POST", ENDPOINT_AUTH_LOGIN).permitAll()
                    
                    // Auth API - Refresh token (uses refreshToken cookie)
                    .requestMatchers("POST", ENDPOINT_AUTH_REFRESH_TOKEN).permitAll()
                    
                    // Auth API - Logout (allows authenticated users to logout)
                    .requestMatchers("POST", ENDPOINT_AUTH_LOGOUT).permitAll()
                    
                    // Patient API - Registration
                    .requestMatchers("POST", ENDPOINT_PATIENT_REGISTER).permitAll()
                    
                    // Professional API - List all professionals (GET only)
                    .requestMatchers("GET", ENDPOINT_PROFESSIONALS).permitAll()
                    
                    // Service API - List all services (GET only)
                    .requestMatchers("GET", ENDPOINT_SERVICES).permitAll()
                    
                    // Availability API - Public access (no authentication required)
                    .requestMatchers("GET", ENDPOINT_AVAILABILITY_DATES).permitAll()
                    .requestMatchers("GET", ENDPOINT_AVAILABILITY_TIME_SLOTS).permitAll()
                    
                    // Swagger UI and API Documentation - ALL PUBLIC
                    .requestMatchers(ENDPOINT_SWAGGER_UI).permitAll()
                    .requestMatchers(ENDPOINT_SWAGGER_CONFIG).permitAll()
                    .requestMatchers(ENDPOINT_API_DOCS).permitAll()
                    .requestMatchers(ENDPOINT_API_DOCS_YAML).permitAll()
                    .requestMatchers(ENDPOINT_WEBJARS).permitAll()
                    
                    // Actuator health check
                    .requestMatchers(ENDPOINT_HEALTH).permitAll()

                    // ========== AUTHENTICATION REQUIRED ==========
                    // Professional API - Registration (ADMIN only)
                    .requestMatchers("POST", ENDPOINT_PROFESSIONAL_REGISTER).hasRole(ROLE_ADMIN)

                    // ========== APPOINTMENT ENDPOINTS ==========
                    // Create appointment (PATIENT only)
                    .requestMatchers("POST", ENDPOINT_APPOINTMENTS).hasRole(ROLE_PATIENT)
                    
                    // List all appointments (ADMIN only)
                    .requestMatchers("GET", ENDPOINT_APPOINTMENTS).hasRole(ROLE_ADMIN)
                    
                    // Get appointment by ID (PATIENT or PROFESSIONAL - their own appointments)
                    .requestMatchers("GET", ENDPOINT_APPOINTMENTS_ID).authenticated()
                    
                    // Confirm appointment (PROFESSIONAL only)
                    .requestMatchers("POST", ENDPOINT_APPOINTMENTS_CONFIRM).hasRole(ROLE_PROFESSIONAL)
                    
                    // Cancel appointment (PATIENT only)
                    .requestMatchers("POST", ENDPOINT_APPOINTMENTS_CANCEL).hasRole(ROLE_PATIENT)
                    
                    // Complete appointment (PROFESSIONAL only)
                    .requestMatchers("POST", ENDPOINT_APPOINTMENTS_COMPLETE).hasRole(ROLE_PROFESSIONAL)
                    
                    // Mark as no-show (PROFESSIONAL only)
                    .requestMatchers("POST", ENDPOINT_APPOINTMENTS_NO_SHOW).hasRole(ROLE_PROFESSIONAL)
                    
                    // Reschedule appointment (PATIENT or PROFESSIONAL)
                    .requestMatchers("PUT", ENDPOINT_APPOINTMENTS_RESCHEDULE).hasAnyRole(ROLE_PATIENT, ROLE_PROFESSIONAL)
                    
                    // Register virtual meeting link (PROFESSIONAL only)
                    .requestMatchers("PUT", ENDPOINT_APPOINTMENTS_VIRTUAL_LINK).hasRole(ROLE_PROFESSIONAL)
                    
                    // List appointments by patient (PATIENT - own appointments only)
                    .requestMatchers("GET", ENDPOINT_APPOINTMENTS_BY_PATIENT).hasRole(ROLE_PATIENT)
                    
                    // List appointments by professional (PROFESSIONAL - own appointments only)
                    .requestMatchers("GET", ENDPOINT_APPOINTMENTS_BY_PROFESSIONAL).hasRole(ROLE_PROFESSIONAL)

                    // ========== ADMIN ENDPOINTS ==========
                    .requestMatchers(ENDPOINT_ADMIN).hasRole(ROLE_ADMIN)
                    .requestMatchers(ENDPOINT_USERS).hasRole(ROLE_ADMIN)

                    // ========== PROFILE ENDPOINTS ==========
                    // Any authenticated user can access their profile
                    .requestMatchers("GET", ENDPOINT_PROFILE).authenticated()

                    // ========== DEFAULT: All other requests require authentication ==========
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(401);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setStatus(403);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"Access denied\"}");
                    })
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:5174"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

package com.nutrisoft.userinterface.api.rest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration for Nutrisoft Backend.
 *
 * <p>Located in: Infrastructure\Config
 *
 * <p>This configuration enables CORS (Cross-Origin Resource Sharing) to allow the React frontend
 * (running on localhost:3000) to make requests to the backend API (running on localhost:8080).
 *
 * <p>Without this configuration, browsers block cross-origin requests for security reasons
 * (same-origin policy).
 *
 * <p>Implementation using WebMvcConfigurer ensures CORS is applied even without Spring Security.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

  /**
   * Configure CORS for all endpoints.
   *
   * <p>This method registers CORS mappings for all API paths. It allows requests from the React
   * frontend and specifies which HTTP methods, headers, and credentials are allowed.
   *
   * @param registry the CorsRegistry to configure
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**") // Apply to all paths
        .allowedOrigins(
            "http://localhost:3000",
            "http://localhost:3001",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:3001")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600); // Cache preflight for 1 hour
  }
}


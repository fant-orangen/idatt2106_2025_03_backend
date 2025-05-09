package stud.ntnu.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@Profile({"unit-test", "test"})  // Active during both unit tests and integration tests
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth ->
                auth
                    // Public endpoints
                    .requestMatchers(
                        "/h2-console/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/actuator/health",
                        "/auth/**",
                        "/api/auth/**",
                        "/api/public/**",
                        "/ws/**"
                    ).permitAll()
                    // Specific public endpoints
                    .requestMatchers("/api/user/confirm-safety").permitAll()
                    // Authenticated user endpoints
                    .requestMatchers("/api/user/**").authenticated()
                    .requestMatchers("/topic/**", "/app/**").authenticated()
                    .requestMatchers("/api/quizzes/**").authenticated()
                    // Admin endpoints
                    .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
                    .requestMatchers("/api/quizzes/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
                    // Super admin endpoints
                    .requestMatchers("/api/super-admin/**").hasRole("SUPERADMIN")
            );

        // Allow H2 console frame options
        http.headers(headers ->
            headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
            Arrays.asList("http://localhost:5173", "http://localhost:8080"));
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
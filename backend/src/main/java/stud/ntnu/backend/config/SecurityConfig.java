package stud.ntnu.backend.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * <h2>SecurityConfig</h2>
 * <p>Central configuration class for Spring Security settings.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * <h3>Password Encoder</h3>
   * <p>Provides BCrypt password encoding functionality.</p>
   *
   * @return configured {@link BCryptPasswordEncoder} instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * <h3>User Details Service</h3>
   * <p>The custom user details service is injected by Spring.</p>
   * <p>It loads users from the database.</p>
   */
  private final UserDetailsService userDetailsService;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(UserDetailsService userDetailsService,
      JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.userDetailsService = userDetailsService;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  /**
   * <h3>Authentication Provider</h3>
   * <p>Configures DAO authentication with user details service and password encoder.</p>
   *
   * @return configured {@link DaoAuthenticationProvider}
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  /**
   * <h3>Authentication Manager</h3>
   * <p>Creates and configures the authentication manager.</p>
   *
   * @param authenticationConfiguration the authentication configuration
   * @return configured {@link AuthenticationManager}
   * @throws Exception if configuration fails
   */
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  /**
   * <h3>Security Filter Chain</h3>
   * <p>Defines the security filter chain configuration.</p>
   *
   * @param http the HTTP security builder
   * @return configured {@link SecurityFilterChain}
   * @throws Exception if configuration fails
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth ->
            auth.requestMatchers("/h2-console/**", "/swagger-ui/**", "/swagger-ui.html",
                    "/v3/api-docs/**", "/actuator/health", "/auth/**", "/api/auth/**", "/api/poi/**",
                    "/poi/**", "/ws/info", "/ws/**", "/api/address/**").permitAll()
                // Allow GET requests to POI endpoints
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/poi/**").permitAll()
                // Permit access to the crisis-events/all endpoint
                .requestMatchers("/api/crisis-events/all").permitAll()
                // Allow all authenticated users to access GET endpoints for specific crisis events
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/crisis-events/{id}")
                .authenticated()
                // Add role-based authorization for admin endpoints
                .requestMatchers("/api/crisis-events/**").hasAnyRole("ADMIN", "SUPERADMIN")
                .requestMatchers("/api/super-admin", "/api/super-admin/**").hasAnyRole("SUPERADMIN")
                .requestMatchers("/api/super-admin", "/api/super-admin//id-by-email/{email}").hasAnyRole("SUPERADMIN")

                // Add WebSocket security
                .requestMatchers("/topic/**", "/app/**").authenticated()
                // Add role-based authorization for create/update/delete points of interest
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/poi/**")
                .hasAnyRole("ADMIN", "SUPERADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/poi/**")
                .hasAnyRole("ADMIN", "SUPERADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/poi/**")
                .hasAnyRole("ADMIN", "SUPERADMIN")
                .anyRequest().authenticated());

    // Allow H2 console frame options
    http.headers(headers ->
        headers.frameOptions(frameOptions -> frameOptions.disable()));

    // Add JWT authentication filter
    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * <h3>CORS Configuration</h3>
   * <p>Configures Cross-Origin Resource Sharing settings.</p>
   *
   * @return configured {@link CorsConfigurationSource}
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        Arrays.asList("http://localhost:5173", "http://localhost:8080"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}

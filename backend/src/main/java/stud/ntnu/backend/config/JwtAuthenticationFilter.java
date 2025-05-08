package stud.ntnu.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import stud.ntnu.backend.util.JwtUtil;

import java.io.IOException;

/**
 * A Spring Security filter that intercepts incoming HTTP requests to validate JWT tokens.
 * This filter extends OncePerRequestFilter to ensure it is executed only once per request.
 * It extracts the JWT token from the Authorization header, validates it, and sets up the
 * Spring Security context if the token is valid.
 *
 * <p>The filter performs the following steps:
 * <ol>
 *   <li>Extracts the JWT token from the Authorization header</li>
 *   <li>Validates the token and extracts the user's email</li>
 *   <li>Loads the user details from the UserDetailsService</li>
 *   <li>Creates an authentication token and sets it in the SecurityContext</li>
 * </ol>
 *
 * @see OncePerRequestFilter
 * @see JwtUtil
 * @see UserDetailsService
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;

  /**
   * Constructs a new JwtAuthenticationFilter with the required dependencies.
   *
   * @param jwtUtil the utility class for JWT operations
   * @param userDetailsService the service for loading user details
   */
  public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Processes each HTTP request to validate the JWT token and set up authentication.
   * This method is called for every request that matches the filter's URL pattern.
   *
   * <p>The method:
   * <ol>
   *   <li>Extracts the JWT token from the Authorization header if present</li>
   *   <li>Attempts to extract the email from the token</li>
   *   <li>If successful and no authentication exists, loads the user details</li>
   *   <li>Validates the token against the user details</li>
   *   <li>If valid, creates an authentication token and sets it in the security context</li>
   * </ol>
   *
   * @param request the HTTP request to process
   * @param response the HTTP response
   * @param filterChain the filter chain to continue processing
   * @throws ServletException if an error occurs during servlet processing
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    final String authorizationHeader = request.getHeader("Authorization");

    String email = null;
    String jwt = null;

    // Extract JWT token from Authorization header
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.substring(7);
      try {
        email = jwtUtil.extractEmail(jwt);
      } catch (Exception e) {
        logger.error("Error extracting email from token", e);
      }
    }

    // Validate token and set authentication if valid
    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

      if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    filterChain.doFilter(request, response);
  }
}
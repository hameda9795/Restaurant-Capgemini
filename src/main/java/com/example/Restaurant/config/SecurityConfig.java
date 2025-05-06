package com.example.Restaurant.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the restaurant application.
 * Handles authentication, authorization, and user access control.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Creates a password encoder for secure password hashing.
     * Uses BCrypt algorithm for password encryption.
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Sets up the authentication manager with custom user details and password encoder.
     * This manages how users are authenticated in the system.
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    /**
     * Helper method to check if a user has access to a specific URL based on their role.
     */
    private boolean hasAccess(Authentication authentication, String url) {
        var roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (url.startsWith("/admin") && roles.contains("ROLE_ADMIN")) return true;
        if (url.startsWith("/waiters") && roles.contains("ROLE_WAITER")) return true;
        if (url.startsWith("/orders") && (roles.contains("ROLE_CHEF") || roles.contains("ROLE_SOUS_CHEF"))) return true;

        return false;
    }

    /**
     * Configures security settings for the application.
     * This includes URL access rules, login behavior, and access denied handling.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for development
                .csrf(AbstractHttpConfigurer::disable)
                // Allow frames for H2 Console
                .headers(headers -> headers.frameOptions().sameOrigin())
                // Configure URL access rules
                .authorizeHttpRequests(auth -> auth
                        // Public pages that don't require login
                        .requestMatchers(
                                "/home",
                                "/register",
                                "/login",
                                "/css/**",
                                "/static/**",
                                "/uploads/**",
                                "/gallery/**",
                                "/about/**",
                                "/js/**",
                                "/h2-console/**",
                                "/img/**",
                                "/menu/**",        // Allow access to all menus
                                "/cart/**",        // Allow access to the cart
                                "/orders/checkout",
                                "/orders/**"
                        ).permitAll()
                        // Admin only pages
                        .requestMatchers("/h2-console/**").hasRole("ADMIN")
                        // Kitchen staff pages
                        .requestMatchers( "/ingredients/**")
                        .hasAnyRole("CHEF")
                        .requestMatchers( "/orders/**", "/ingredients/**", "/menu/order").hasRole("SOUS_CHEF")
                        // Waiter only pages
                        .requestMatchers( "/menu/order/**").hasRole("WAITER")
                        // All other pages require authentication
                        .anyRequest().authenticated()
                )
                // Configure access denied handling
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Save the original URL for redirect after login
                            String targetUrl = request.getRequestURI();
                            request.getSession().setAttribute("REDIRECT_URL", targetUrl);
                            // Redirect to login page with message
                            response.sendRedirect("/login?message=Please login with appropriate role");
                        })
                )
                // Configure login settings
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            // Check for saved redirect URL
                            String redirectUrl = (String) request.getSession().getAttribute("REDIRECT_URL");
                            if (redirectUrl != null) {
                                request.getSession().removeAttribute("REDIRECT_URL");
                                // Redirect to saved URL if user has access
                                if (hasAccess(authentication, redirectUrl)) {
                                    response.sendRedirect(redirectUrl);
                                    return;
                                }
                            }

                            // Default redirects based on user role
                            var roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
                            String redirectURL = roles.contains("ROLE_ADMIN") ? "/admin/dashboard" :
                                    roles.contains("ROLE_WAITER") ? "/dashboard/waiter-dashboard" :
                                            roles.contains("ROLE_CHEF") ? "/kitchen" :
                                                    roles.contains("ROLE_SOUS_CHEF") ? "dashboard/souschef-dashboard":
                                                            "/";
                            response.sendRedirect(redirectURL);
                        })
                )
                // Configure logout settings
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }
}
package org.example.restaurantms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource()).and()
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/auth/**",
                                "/error"
                        ).permitAll()

                        // ————— deliveries: /api/deliveries/*  (tylko ADMIN) —————
                        .requestMatchers(HttpMethod.GET, "/api/deliveries/get").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/deliveries/post").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/deliveries/delete/**").hasAuthority("ROLE_ADMINISTRATOR")

                        // ————— menu: /api/menu/* —————
                        .requestMatchers(HttpMethod.GET, "/api/menu/get").hasAnyAuthority("ROLE_ADMINISTRATOR","ROLE_USER")
                        .requestMatchers(HttpMethod.POST, "/api/menu/post").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/menu/patch/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/menu/delete/**").hasAuthority("ROLE_ADMINISTRATOR")

                        // ————— orders: /api/orders/* —————
                        .requestMatchers(HttpMethod.POST, "/api/orders/post").hasAnyAuthority("ROLE_ADMINISTRATOR","ROLE_USER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/get").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.GET, "/api/orders/get/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/delete/**").hasAuthority("ROLE_ADMINISTRATOR")

                        // ————— tables: /api/tables/* —————
                        .requestMatchers(HttpMethod.GET, "/api/tables/get").hasAnyAuthority("ROLE_ADMINISTRATOR","ROLE_USER")
                        .requestMatchers(HttpMethod.GET, "/api/tables/get/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/tables/create").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/tables/patch/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/tables/delete/**").hasAuthority("ROLE_ADMINISTRATOR")

                        // ————— reservations: /api/reservations/* —————
                        .requestMatchers(HttpMethod.GET, "/api/reservations/get").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/reservations/post").hasAnyAuthority("ROLE_ADMINISTRATOR","ROLE_USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/reservations/patch/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/delete/**").hasAuthority("ROLE_ADMINISTRATOR")

                        .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMINISTRATOR")


                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .httpBasic();

        return http.build();
    }

}

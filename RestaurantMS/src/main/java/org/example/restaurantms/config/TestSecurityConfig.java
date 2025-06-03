package org.example.restaurantms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("test")
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth

                        // auth publiczne
                        .requestMatchers("/api/auth/**").permitAll()

                        // deliveries (ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/deliveries/get").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/deliveries/post").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/deliveries/delete/**").hasAuthority("ROLE_ADMINISTRATOR")

                        // menu
                        .requestMatchers(HttpMethod.GET, "/api/menu/get").hasAnyAuthority("ROLE_ADMINISTRATOR", "ROLE_USER")
                        .requestMatchers(HttpMethod.POST, "/api/menu/post").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/menu/patch/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/menu/delete/**").hasAuthority("ROLE_ADMINISTRATOR")

                        // orders
                        .requestMatchers(HttpMethod.POST, "/api/orders/post").hasAnyAuthority("ROLE_ADMINISTRATOR", "ROLE_USER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/get").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.GET, "/api/orders/get/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/delete/**").hasAuthority("ROLE_ADMINISTRATOR")

                        // tables
                        .requestMatchers(HttpMethod.GET, "/api/tables/get").hasAnyAuthority("ROLE_ADMINISTRATOR", "ROLE_USER")
                        .requestMatchers(HttpMethod.GET, "/api/tables/get/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/tables/create").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/tables/patch/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/tables/delete/**").hasAuthority("ROLE_ADMINISTRATOR")

                        // reservations
                        .requestMatchers(HttpMethod.GET, "/api/reservations/get").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/reservations/post").hasAnyAuthority("ROLE_ADMINISTRATOR", "ROLE_USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/reservations/patch/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/delete/**").hasAuthority("ROLE_ADMINISTRATOR")

                        // users
                        .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMINISTRATOR")

                        .anyRequest().authenticated()
                )
                .httpBasic();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}

package ru.netology.cloudservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity

public class SecurityConfig {
    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests(authorizeRequests ->
//                        authorizeRequests
//                                .anyRequest().permitAll());
        http
                .csrf(csrf -> csrf.disable()) // Отключение CSRF защиты
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // Разрешить все запросы
                );
                // Включить форму входа (если требуется)


        return http.build();
    }
}







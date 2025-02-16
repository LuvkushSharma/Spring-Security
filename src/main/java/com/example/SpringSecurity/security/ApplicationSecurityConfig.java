package com.example.SpringSecurity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig {

     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http
                 .csrf(csrf -> csrf.disable())  // Disabling CSRF (for testing, not recommended in production)
                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers("/login" , "/register").permitAll()  // Allow public URLs
                         .anyRequest().authenticated()  // Protect all other URLs
                 )
                 .formLogin(form -> form
                         .defaultSuccessUrl("/home", true)  // Redirect to /home after successful login
                 );

         return http.build();
     }
}

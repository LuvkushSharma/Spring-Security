package com.example.SpringSecurity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;




@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig {

     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http
                 .csrf(csrf -> csrf.disable())  // 🔴 Disable CSRF (for Postman/REST APIs)
                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers("/login" , "/register" , "/public/**").permitAll()  // ✅ Public endpoints
                         .requestMatchers("/api/**").hasRole("STUDENT")  // ✅ Student role required for APIs
                         .requestMatchers("/admin/**").hasRole("ADMIN")  // ✅ Admin role required
                         .anyRequest().authenticated()  // Protect all other URLs
                 )
                 .httpBasic(Customizer.withDefaults())  // 🔴 Basic Authentication
                 .formLogin(login -> login.disable());  // 🔴 Disable default Spring Security login page


         return http.build();
     }

     @Bean
     public UserDetailsService userDetailsService() {

         UserDetails user1 = User.withUsername("user1")
                 .password(passwordEncoder().encode("password")) // Encrypt password
                 .roles("STUDENT")
                 .build();

         UserDetails user2 = User.withUsername("admin")
                 .password(passwordEncoder().encode("admin123"))
                 .roles("ADMIN")
                 .build();

         return new InMemoryUserDetailsManager(user1, user2);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}

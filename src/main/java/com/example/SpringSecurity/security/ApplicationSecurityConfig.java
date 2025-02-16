package com.example.SpringSecurity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static com.example.SpringSecurity.security.ApplicationUserRole.*;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // This is used to enable method level security ----> @PreAuthorize("hasRole('ROLE_')") or @PreAuthorize("hasAuthority('permission')") ------> M-2
public class ApplicationSecurityConfig {

     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

         http
                 .csrf(csrf -> csrf.disable())  // 🔴 Disable CSRF (for Postman/REST APIs)
                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers("/", "index", "/css/*", "/js/*").permitAll()  // ✅ Public endpoints
                         .requestMatchers("/api/**").hasRole(STUDENT.name())  // ✅ Student role required for APIs
                         .requestMatchers("/admin/**").hasRole(ADMIN.name())  // ✅ Admin role required
                         .anyRequest().authenticated()  // Protect all other URLs
                 )
                 .formLogin(form -> form
                         .loginPage("/login")  // Custom login page
                         .defaultSuccessUrl("/" , true)  // Redirect to home page after successful login
                         .permitAll()
                 );


         return http.build();
     }

     @Bean
     public UserDetailsService userDetailsService() {

         UserDetails user1 = User.withUsername("student1")
                 .password(passwordEncoder().encode("password")) // Encrypt password
                 .authorities(STUDENT.getGrantedAuthorities())  // STUDENT.getGrantedAuthorities() returns the permissions of the user
                 .build();

         UserDetails user2 = User.withUsername("admin")
                 .password(passwordEncoder().encode("admin123"))
                 .authorities(ADMIN.getGrantedAuthorities())  // ADMIN.getGrantedAuthorities() returns the permissions of the user
                 .build();

         UserDetails user3 = User.withUsername("admintrainee")
                 .password(passwordEncoder().encode("adminTrainee123"))
                 .authorities(ADMINTRAINEE.getGrantedAuthorities())  // ADMINTRAINEE.getGrantedAuthorities() returns the permissions of the user
                 .build();

         return new InMemoryUserDetailsManager(user1, user2 , user3);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}

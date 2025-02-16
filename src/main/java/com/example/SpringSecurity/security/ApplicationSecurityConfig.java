package com.example.SpringSecurity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import static com.example.SpringSecurity.security.ApplicationUserPermission.*;
import static com.example.SpringSecurity.security.ApplicationUserRole.*;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // This is used to enable method level security ----> @PreAuthorize("hasRole('ROLE_')") or @PreAuthorize("hasAuthority('permission')") ------> M-2
public class ApplicationSecurityConfig {

     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

         // M-1 : This is the method-1 for permission based authentication
         http
                 .csrf(csrf -> csrf.disable())  // 🔴 Disable CSRF (for Postman/REST APIs)
                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers("/login" , "/register" , "/public/**").permitAll()  // ✅ Public endpoints
                         .requestMatchers("/api/**").hasRole(STUDENT.name())  // ✅ Student role required for APIs
                         .requestMatchers("/admin/**").hasRole(ADMIN.name())  // ✅ Admin role required
//                         .requestMatchers(HttpMethod.DELETE , "/management/api/**").hasAuthority(COURSE_WRITE.getPermission()) // Any user with COURSE_WRITE authority can delete
//                         .requestMatchers(HttpMethod.POST , "/management/api/**").hasAuthority(COURSE_WRITE.getPermission()) // Any user with COURSE_WRITE authority can post
//                         .requestMatchers(HttpMethod.PUT , "/management/api/**").hasAuthority(COURSE_WRITE.getPermission()) // Any user with COURSE_WRITE authority can put
//                         .requestMatchers(HttpMethod.GET , "/management/api/**").hasAnyRole(ADMIN.name() , ADMINTRAINEE.name())  // Admin and Admin Trainee can access management APIs
                         .anyRequest().authenticated()  // Protect all other URLs
                 )
                 .httpBasic(Customizer.withDefaults())  // 🔴 Basic Authentication
                 .formLogin(login -> login.disable());  // 🔴 Disable default Spring Security login page


         return http.build();
     }

     @Bean
     public UserDetailsService userDetailsService() {

         UserDetails user1 = User.withUsername("student1")
                 .password(passwordEncoder().encode("password")) // Encrypt password
//                 .roles(STUDENT.name())  // ROLE_STUDENT
                 .authorities(STUDENT.getGrantedAuthorities())  // STUDENT.getGrantedAuthorities() returns the permissions of the user
                 .build();

         UserDetails user2 = User.withUsername("admin")
                 .password(passwordEncoder().encode("admin123"))
//                 .roles(ADMIN.name())  // ROLE_ADMIN
                 .authorities(ADMIN.getGrantedAuthorities())  // ADMIN.getGrantedAuthorities() returns the permissions of the user
                 .build();

         // ADMINTRAINEE role is a trainee for the ADMIN role. So, we will give him permission to read request only for the management/api/v1/students.
         UserDetails user3 = User.withUsername("admintrainee")
                 .password(passwordEncoder().encode("adminTrainee123"))
//                 .roles(ADMINTRAINEE.name())  // ROLE_ADMINTRAINEE
                 .authorities(ADMINTRAINEE.getGrantedAuthorities())  // ADMINTRAINEE.getGrantedAuthorities() returns the permissions of the user
                 .build();

         return new InMemoryUserDetailsManager(user1, user2 , user3);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}

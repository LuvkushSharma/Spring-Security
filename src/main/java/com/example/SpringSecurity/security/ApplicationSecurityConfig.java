package com.example.SpringSecurity.security;

import com.example.SpringSecurity.auth.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Duration;

import static com.example.SpringSecurity.security.ApplicationUserRole.*;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // This is used to enable method level security ----> @PreAuthorize("hasRole('ROLE_')") or @PreAuthorize("hasAuthority('permission')") ------> M-2
public class ApplicationSecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder,
                                     ApplicationUserService applicationUserService) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
    }

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
                 )
                 .rememberMe(rememberMe -> rememberMe
                         .tokenValiditySeconds((int) Duration.ofDays(21).getSeconds())  // Remember me for 21 days
                         .key("secureKey")  // Key to encrypt remember me cookie
                 )
                 .logout(logout -> logout
                         .logoutUrl("/logout")  // Custom logout URL
                         .clearAuthentication(true)  // Clear authentication
                         .invalidateHttpSession(true)  // Invalidate session
                         .deleteCookies("JSESSIONID", "remember-me")  // Delete cookies
                         .logoutSuccessUrl("/login")  // Redirect to login page after logout
                 );


         return http.build();
     }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);

        provider.setUserDetailsService(applicationUserService);
        return provider;
    }
}

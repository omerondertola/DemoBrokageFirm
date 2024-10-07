package com.example.demo.configuration;

import com.example.demo.login.AppUserDetailsService;
import com.example.demo.customers.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

    private final CustomerRepo userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var ap = new DaoAuthenticationProvider();
        ap.setUserDetailsService(new AppUserDetailsService(userRepository));
        ap.setPasswordEncoder(passwordEncoder());
        return ap;
    }

    /*
    @Bean
    public AppAuthorizationManager myAuthorizationManager() {
        return new AppAuthorizationManager();
    }
     */


}

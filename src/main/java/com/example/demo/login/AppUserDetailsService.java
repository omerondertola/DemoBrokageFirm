package com.example.demo.login;

import com.example.demo.customers.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final CustomerRepo userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("loading..user..by username(email): "+email);
        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException("not found in database: "+email)
                );
    }
}

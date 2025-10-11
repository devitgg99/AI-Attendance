package com.example.attendancemanagement.config;

import com.example.attendancemanagement.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@Configuration
public class SecurityBeans {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByEmail(username)
                .map(user -> {
                    String role = getUserRoleFromMap(user.getUserInfo());
                    return (UserDetails) org.springframework.security.core.userdetails.User
                            .withUsername(user.getEmail())
                            .password(user.getPassword())
                            .authorities("ROLE_" + role.toUpperCase())
                            .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Helper method to extract role from user_info Map
    private String getUserRoleFromMap(Map<String, Object> userInfoMap) {
        if (userInfoMap == null || userInfoMap.isEmpty()) {
            return "student"; // default role
        }
        Object role = userInfoMap.get("role");
        return role != null ? role.toString() : "student";
    }
}




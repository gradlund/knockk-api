package com.knockk.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration class for the BCryptPasswordEncoder
 */
@Configuration
public class AppConfig {
    /**
     * Password encoder bean
     * 
     * @return the BCrypstPasswordEncoder for hashing
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

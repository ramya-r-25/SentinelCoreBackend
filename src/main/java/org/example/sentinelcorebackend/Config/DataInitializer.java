package org.example.sentinelcorebackend.Config;


import org.example.sentinelcorebackend.Entity.Role;
import org.example.sentinelcorebackend.Entity.User;
import org.example.sentinelcorebackend.Repository.RoleRepository;
import org.example.sentinelcorebackend.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Create roles
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() ->
                            roleRepository.save(
                                    Role.builder()
                                            .name("ROLE_ADMIN")
                                            .build()
                            )
                    );

            Role operatorRole = roleRepository.findByName("ROLE_OPERATOR")
                    .orElseGet(() ->
                            roleRepository.save(
                                    Role.builder()
                                            .name("ROLE_OPERATOR")
                                            .build()
                            )
                    );

            Role viewerRole = roleRepository.findByName("ROLE_VIEWER")
                    .orElseGet(() ->
                            roleRepository.save(
                                    Role.builder()
                                            .name("ROLE_VIEWER")
                                            .build()
                            )
                    );

            // Create admin user
            if (userRepository.findByUsername("admin").isEmpty()) {

                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .email("admin@sentinelcore.local")
                        .roles(Set.of(adminRole))
                        .enabled(true)
                        .build();

                userRepository.save(admin);

                System.out.println("Admin user created successfully.");
            }
        };
    }
}

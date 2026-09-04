package org.example.sentinelcorebackend.Config;

import org.example.sentinelcorebackend.Entity.Role;
import org.example.sentinelcorebackend.Entity.User;
import org.example.sentinelcorebackend.Repository.RoleRepository;
import org.example.sentinelcorebackend.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Create roles if they don't exist
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));

            Role operatorRole = roleRepository.findByName("ROLE_OPERATOR")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_OPERATOR").build()));

            Role viewerRole = roleRepository.findByName("ROLE_VIEWER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_VIEWER").build()));

            // 1. Initialize Admin user (admin / admin123)
            Optional<User> adminOpt = userRepository.findByUsername("admin");
            User admin = adminOpt.orElseGet(() -> User.builder().username("admin").build());
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@sentinelcore.local");
            admin.setRoles(Set.of(adminRole));
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("Admin user initialized successfully.");

            // 2. Initialize Operator user (operator / operator123)
            Optional<User> operatorOpt = userRepository.findByUsername("operator");
            User operator = operatorOpt.orElseGet(() -> User.builder().username("operator").build());
            operator.setPassword(passwordEncoder.encode("operator123"));
            operator.setEmail("operator@sentinelcore.local");
            operator.setRoles(Set.of(operatorRole));
            operator.setEnabled(true);
            userRepository.save(operator);
            System.out.println("Operator user initialized successfully.");

            // 3. Initialize Viewer user (viewer / viewer123)
            Optional<User> viewerOpt = userRepository.findByUsername("viewer");
            User viewer = viewerOpt.orElseGet(() -> User.builder().username("viewer").build());
            viewer.setPassword(passwordEncoder.encode("viewer123"));
            viewer.setEmail("viewer@sentinelcore.local");
            viewer.setRoles(Set.of(viewerRole));
            viewer.setEnabled(true);
            userRepository.save(viewer);
            System.out.println("Viewer user initialized successfully.");
        };
    }
}
package com.codecool.solarwatch.configuration;

import com.codecool.solarwatch.model.user.Role;
import com.codecool.solarwatch.model.user.UserEntity;
import com.codecool.solarwatch.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Check if admin user already exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserEntity admin = new UserEntity(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "admin@example.com"
            );
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Created admin user with username: admin and password: admin123");
        }
    }
}

package com.shopmart.config;

import com.shopmart.module.user.entity.Role;
import com.shopmart.module.user.entity.User;
import com.shopmart.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds a default admin account on first run if it does not already exist.
 * Override the credentials with ADMIN_EMAIL / ADMIN_PASSWORD env vars.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@shopmart.local}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@12345}")
    private String adminPassword;

    @Value("${app.superadmin.email:superadmin@shopmart.local}")
    private String superAdminEmail;

    @Value("${app.superadmin.password:Super@12345}")
    private String superAdminPassword;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedSuperAdmin();
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }
        User admin = new User();
        admin.setName("ShopMart Admin");
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setEmailVerified(true);
        admin.setEnabled(true);
        admin.addRole(Role.ROLE_ADMIN);
        admin.addRole(Role.ROLE_CUSTOMER);
        userRepository.save(admin);
        log.info("Seeded default admin account: {}", adminEmail);
    }

    private void seedSuperAdmin() {
        if (userRepository.existsByEmail(superAdminEmail)) {
            return;
        }
        User su = new User();
        su.setName("Super Admin");
        su.setEmail(superAdminEmail);
        su.setPasswordHash(passwordEncoder.encode(superAdminPassword));
        su.setEmailVerified(true);
        su.setEnabled(true);
        su.addRole(Role.ROLE_SUPER_ADMIN);
        su.addRole(Role.ROLE_ADMIN);
        su.addRole(Role.ROLE_CUSTOMER);
        userRepository.save(su);
        log.info("Seeded default super admin account: {}", superAdminEmail);
    }
}

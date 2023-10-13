package com.mycopmany.myproject.machineapi.config;

import com.mycopmany.myproject.machineapi.user.Role;
import com.mycopmany.myproject.machineapi.user.User;
import com.mycopmany.myproject.machineapi.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CommandLineAppStartupRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        User admin = new User("admin",
                "admin",
                "admin",
                passwordEncoder.encode("admin"),
                Role.ADMIN);
        userRepository.save(admin);

    }
}

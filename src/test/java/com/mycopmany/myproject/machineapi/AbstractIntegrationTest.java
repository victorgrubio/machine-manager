package com.mycopmany.myproject.machineapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import java.util.List;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public abstract class AbstractIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.1.0")
            .withReuse(true);

    static {
        mysql.setPortBindings(List.of("3306:3306"));
        mysql.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

    }
}

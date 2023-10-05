package com.mycopmany.myproject.machineapi.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.mycopmany.myproject.machineapi.user.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@Transactional
class AuthenticationControllerIntTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserServiceImpl userService;
    @Container
    private static final MySQLContainer container = new MySQLContainer("mysql:8.1.0");

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);

    }
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void registerValidUser() throws Exception {
        UserToCreate userToCreate = new UserToCreate("firstname",
                "lastname",
                "username",
                "password");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andReturn();
        String jwToken = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/machines")
                        .header("Authorization", "Bearer " + jwToken))
                .andExpect(status().isOk());
        UserDetails userInDatabase = userService.loadUserByUsername(userToCreate.getUsername());
        assertEquals(userToCreate.getUsername(), userInDatabase.getUsername());
    }

    @Test
    void registerInvalidUser() throws Exception {
        UserToCreate userToCreate = new UserToCreate("firstname",
                "lastname",
                "username",
                "password");
        authenticationService.register(userToCreate);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isConflict());
    }

    @Test
    void authenticateValidUser() throws Exception {
        UserToCreate userToCreate = new UserToCreate("firstname",
                "lastname",
                "username",
                "password");
        UserToLogin userToLogin = new UserToLogin("username","password");
        authenticationService.register(userToCreate);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToLogin)))
                .andExpect(status().isOk())
                .andReturn();
        String jwToken = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/machines")
                        .header("Authorization", "Bearer " + jwToken))
                .andExpect(status().isOk());
    }

    @Test
    void authenticateInvalidUser() throws Exception {
        UserToLogin userToLogin = new UserToLogin("username","password");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToLogin)))
                .andExpect(status().isUnauthorized());
    }
}
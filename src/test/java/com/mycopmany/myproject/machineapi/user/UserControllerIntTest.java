package com.mycopmany.myproject.machineapi.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycopmany.myproject.machineapi.auth.AuthenticationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@Transactional
class UserControllerIntTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserRepository userRepository;

    @Container
    private static final MySQLContainer container = new MySQLContainer("mysql:8.1.0");
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);

    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void getUsers() throws Exception {
        UserToCreate userToCreate = new UserToCreate(
                "firstname",
                "lastname",
                "username",
                "password");
        authenticationService.register(userToCreate);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/users")
                .header("Authorization", "Bearer " + "token"))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        UserToGet[] usersToGet = objectMapper.readValue(jsonResponse, UserToGet[].class);

        assertEquals(1,usersToGet.length);
        assertEquals(usersToGet[0].getFirstName(),userToCreate.getFirstName());
        assertEquals(usersToGet[0].getLastName(),userToCreate.getLastName());
        assertEquals(usersToGet[0].getUsername(),userToCreate.getUsername());

    }

    @Test
    void deleteUserWhenExists() throws Exception {
        UserToCreate userToCreate1 = new UserToCreate(
                "firstname",
                "lastname",
                "username",
                "password");

        UserToCreate userToCreate2 = new UserToCreate(
                "firstname2",
                "lastname2",
                "username2",
                "password2");
        authenticationService.register(userToCreate1);
        authenticationService.register(userToCreate2);
        Long idToDelete = 1L;
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/" + idToDelete )
                        .header("Authorization", "Bearer " + "token"))
                .andExpect(status().isNoContent());

        boolean userExists = userRepository.existsById(idToDelete);
        assertFalse(userExists);
        assertEquals(1, userRepository.count());
    }
    @Test
    void deleteUserWhenDoesNotExist() throws Exception {
        long idToDelete = 1L;
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/" + idToDelete )
                        .header("Authorization", "Bearer " + "token"))
                .andExpect(status().isNotFound());

    }
}
package com.mycopmany.myproject.machineapi.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycopmany.myproject.machineapi.AbstractIntegrationTest;
import com.mycopmany.myproject.machineapi.auth.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MySQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntTest extends AbstractIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserRepository userRepository;

    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.1.0")
            .withReuse(true);
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

    }

    static {
        mysql.setPortBindings(List.of("3306:3306"));
        mysql.start();
    }

    @BeforeEach
    void setUp() {
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

        assertEquals(2, usersToGet.length);
        assertEquals("admin", usersToGet[0].getFirstName());
        assertEquals("admin", usersToGet[0].getLastName());
        assertEquals("admin", usersToGet[0].getUsername());

    }

    @Test
    void deleteUserWhenExists() throws Exception {
        UserToCreate userToCreate1 = new UserToCreate(
                "firstname",
                "lastname",
                "username",
                "password");
        authenticationService.register(userToCreate1);
        Long idToDelete = 1L;
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/" + idToDelete)
                        .header("Authorization", "Bearer " + "token"))
                .andExpect(status().isNoContent());

        boolean userExists = userRepository.existsById(idToDelete);
        assertFalse(userExists);
        assertEquals(1, userRepository.count());
    }

    @Test
    void deleteUserWhenDoesNotExist() throws Exception {
        long idToDelete = 1293L;
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/" + idToDelete)
                        .header("Authorization", "Bearer " + "token"))
                .andExpect(status().isNotFound());

    }
}
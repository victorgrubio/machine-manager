package com.mycopmany.myproject.machineapi.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.mycopmany.myproject.machineapi.AbstractIntegrationTest;
import com.mycopmany.myproject.machineapi.user.UserServiceImpl;
import com.mycopmany.myproject.machineapi.user.UserToCreate;
import com.mycopmany.myproject.machineapi.user.UserToLogin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerIntTest extends AbstractIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserServiceImpl userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
    }
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void registerValidUser() throws Exception {
        UserToCreate userToCreate = new UserToCreate("firstname",
                "lastname",
                "username",
                "password");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isCreated());

        UserDetails userInDatabase = userService.loadUserByUsername(userToCreate.getUsername());
        assertEquals(userToCreate.getUsername(), userInDatabase.getUsername());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
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
        UserToLogin userToLogin = new UserToLogin("username", "password");
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
        UserToLogin userToLogin = new UserToLogin("username", "password");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToLogin)))
                .andExpect(status().isUnauthorized());
    }
}
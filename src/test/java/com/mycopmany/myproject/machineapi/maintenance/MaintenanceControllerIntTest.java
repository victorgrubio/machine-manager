package com.mycopmany.myproject.machineapi.maintenance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.mycopmany.myproject.machineapi.auth.AuthenticationService;
import com.mycopmany.myproject.machineapi.machine.MachineService;
import com.mycopmany.myproject.machineapi.machine.MachineToCreate;
import com.mycopmany.myproject.machineapi.user.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MaintenanceControllerIntTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Container
    private static final MySQLContainer container = new MySQLContainer("mysql:8.1.0");

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);

    }

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MaintenanceService maintenanceService;
    @Autowired
    private MachineService machineService;
    @Autowired
    private AuthenticationService authenticationService;
    private String jwToken;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
        UserToCreate userToCreate = new UserToCreate(
                "firstname",
                "lastname",
                "username",
                "password"
        );
        authenticationService.register(userToCreate);

        UserToLogin userToLogin = new UserToLogin("username","password");
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToLogin)))
                .andExpect(status().isOk())
                .andReturn();
        jwToken = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");

    }

    @Test
    void createAndGetMaintenanceRecord() throws Exception {
        MachineToCreate machineToCreate = new MachineToCreate(123L,
                "model",
                "category",
                "location");
        machineService.createMachine(machineToCreate);
        MaintenanceRecordToCreate maintenanceRecordToCreate = new MaintenanceRecordToCreate(
                "title",
                "description",
                123L
        );
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/maintenance-records")
                        .header("Authorization", "Bearer " + jwToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(maintenanceRecordToCreate)))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/maintenance-records")
                        .header("Authorization", "Bearer " + jwToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description")
                        .value("description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].technicianName")
                        .value("firstname lastname"));
    }
    @Test
    void createRecordWhenMachineDoesNotExist() throws Exception {
        MaintenanceRecordToCreate maintenanceRecordToCreate = new MaintenanceRecordToCreate(
                "title",
                "description",
                123L
        );
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/maintenance-records")
                        .header("Authorization", "Bearer " + jwToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maintenanceRecordToCreate)))
                .andExpect(status().isNotFound());
    }
    @Test
    void createRecordWhenTitleIsEmpty() throws Exception {
        MachineToCreate machineToCreate = new MachineToCreate(123L,
                "model",
                "category",
                "location");
        machineService.createMachine(machineToCreate);
        MaintenanceRecordToCreate maintenanceRecordToCreate = new MaintenanceRecordToCreate(
                "",
                "description",
                123L);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/maintenance-records")
                        .header("Authorization", "Bearer " + jwToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maintenanceRecordToCreate)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createAndGetMaintenanceRecordsBySerialNumber() throws Exception{
        MachineToCreate machineToCreate = new MachineToCreate(123L,
                "model",
                "category",
                "location");
        machineService.createMachine(machineToCreate);
        MaintenanceRecordToCreate maintenanceRecordToCreate = new MaintenanceRecordToCreate(
                "title",
                "description",
                123L);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/maintenance-records")
                        .header("Authorization", "Bearer " + jwToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maintenanceRecordToCreate)))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/maintenance-records/by-machine/"
                                + machineToCreate.getSerialNumber())
                        .header("Authorization", "Bearer " + jwToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description")
                        .value("description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].technicianName")
                        .value("firstname lastname"));

    }

    @Test
    void getMaintenanceByMachineWhenMachineDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/maintenance-records/by-machine/"
                                + 123L)
                        .header("Authorization", "Bearer " + jwToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void editRecordWhenExist() throws Exception {
        MaintenanceRecordToEdit maintenanceRecordToEdit = new MaintenanceRecordToEdit(
                "newTitle",
                "newDescription"
        );
        MachineToCreate machineToCreate = new MachineToCreate(123L,
                "model",
                "category",
                "location");
        machineService.createMachine(machineToCreate);
        MaintenanceRecordToCreate maintenanceRecordToCreate = new MaintenanceRecordToCreate(
                "title",
                "description",
                123L);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/maintenance-records")
                        .header("Authorization", "Bearer " + jwToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maintenanceRecordToCreate)))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/maintenance-records/" + 1L)
                        .header("Authorization", "Bearer " + jwToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maintenanceRecordToEdit)))
                .andExpect(status().isOk());

        List<MaintenanceRecordToGet> result = maintenanceService.getRecordsByMachine(123L);
        assertEquals(1,result.size());
        assertEquals("newTitle",result.get(0).getTitle());
        assertEquals("newDescription",result.get(0).getDescription());
        assertEquals(123L,result.get(0).getMachineId());
        assertEquals("firstname lastname",result.get(0).getTechnicianName());
    }
    @Test
    void editRecordWhenDoesNotExist() throws Exception {
        MaintenanceRecordToEdit maintenanceRecordToEdit = new MaintenanceRecordToEdit(
                "newTitle",
                "newDescription"
        );
        long idToEdit = 3L;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/maintenance-records/" + idToEdit)
                        .header("Authorization", "Bearer " + jwToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maintenanceRecordToEdit)))
                .andExpect(status().isNotFound());

    }

    @Test
    void deleteRecord() throws Exception {
        MachineToCreate machineToCreate = new MachineToCreate(123L,
                "model",
                "category",
                "location");
        machineService.createMachine(machineToCreate);
        MaintenanceRecordToCreate maintenanceRecordToCreate = new MaintenanceRecordToCreate(
                "title",
                "description",
                123L);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/maintenance-records")
                        .header("Authorization", "Bearer " + jwToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maintenanceRecordToCreate)))
                .andExpect(status().isCreated());
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/maintenance-records/" + 1L)
                .header("Authorization", "Bearer " + jwToken))
                .andExpect(status().isNoContent());
    }
    @Test
    void deleteRecordWhenDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/maintenance-records/" + 1L)
                        .header("Authorization", "Bearer " + jwToken))
                .andExpect(status().isNotFound());

    }
}
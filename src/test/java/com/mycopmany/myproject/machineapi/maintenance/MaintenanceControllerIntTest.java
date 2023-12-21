package com.mycopmany.myproject.machineapi.maintenance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.mycopmany.myproject.machineapi.AbstractIntegrationTest;
import com.mycopmany.myproject.machineapi.auth.AuthenticationService;
import com.mycopmany.myproject.machineapi.machine.MachineService;
import com.mycopmany.myproject.machineapi.machine.MachineToCreate;
import com.mycopmany.myproject.machineapi.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

class MaintenanceControllerIntTest extends AbstractIntegrationTest {
        @Autowired
        private WebApplicationContext webApplicationContext;
        @Autowired
        private MaintenanceService maintenanceService;
        @Autowired
        private MachineService machineService;
        @Autowired
        private AuthenticationService authenticationService;
        @Autowired
        private ObjectMapper objectMapper;
        private String jwToken;
        private MockMvc mockMvc;

        @BeforeEach
        void setUp() throws Exception {
                String username = "username";
                String password = "password";
                this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                                .apply(springSecurity())
                                .build();
                UserToCreate userToCreate = new UserToCreate(
                                "firstname",
                                "lastname",
                                username,
                                password);
                authenticationService.register(userToCreate);

                UserToLogin userToLogin = new UserToLogin(username, password);
                MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                                .post("/api/v1/auth/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userToLogin)))
                                .andExpect(status().isOk())
                                .andReturn();
                jwToken = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.token");
        }

        @Test
        void createAndGetMaintenance() throws Exception {
                MachineToCreate machineToCreate = new MachineToCreate(123L,
                                "model",
                                "category",
                                "location");
                machineService.createMachine(machineToCreate);
                MaintenanceToCreate maintenanceToCreate = new MaintenanceToCreate(
                                "title",
                                "description",
                                123L);
                mockMvc.perform(MockMvcRequestBuilders
                                .post("/api/v1/maintenance-records")
                                .header("Authorization", "Bearer " + jwToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(maintenanceToCreate)))
                                .andExpect(status().isCreated());

                mockMvc.perform(MockMvcRequestBuilders
                                .get("/api/v1/maintenance-records")
                                .header("Authorization", "Bearer " + jwToken))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("title"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description")
                                                .value("description"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].technicianName")
                                                .value("firstname lastname"));
        }

        @Test
        void createMaintenanceWhenMachineDoesNotExist() throws Exception {
                MaintenanceToCreate maintenanceToCreate = new MaintenanceToCreate(
                                "title",
                                "description",
                                123L);
                mockMvc.perform(MockMvcRequestBuilders
                                .post("/api/v1/maintenance-records")
                                .header("Authorization", "Bearer " + jwToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(maintenanceToCreate)))
                                .andExpect(status().isNotFound());
        }
        // @Test
        // void createMaintenanceWhenTitleIsEmpty() throws Exception {
        // MachineToCreate machineToCreate = new MachineToCreate(123L,
        // "model",
        // "category",
        // "location");
        // machineService.createMachine(machineToCreate);
        // MaintenanceToCreate maintenanceToCreate = new MaintenanceToCreate(
        // "",
        // "description",
        // 123L);

        // mockMvc.perform(MockMvcRequestBuilders
        // .post("/api/v1/maintenance-records")
        // .header("Authorization", "Bearer " + jwToken)
        // .contentType(MediaType.APPLICATION_JSON)
        // .content(objectMapper.writeValueAsString(maintenanceToCreate)))
        // .andDo(MockMvcResultHandlers.print())
        // .andExpect(status().isUnprocessableEntity());
        // }

        @Test
        void createAndGetMaintenanceBySerialNumber() throws Exception {
                MachineToCreate machineToCreate = new MachineToCreate(123L,
                                "model",
                                "category",
                                "location");
                machineService.createMachine(machineToCreate);
                MaintenanceToCreate maintenanceToCreate = new MaintenanceToCreate(
                                "title",
                                "description",
                                123L);
                mockMvc.perform(MockMvcRequestBuilders
                                .post("/api/v1/maintenance-records")
                                .header("Authorization", "Bearer " + jwToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(maintenanceToCreate)))
                                .andExpect(status().isCreated());

                mockMvc.perform(MockMvcRequestBuilders
                                .get("/api/v1/maintenance-records/by-machine/"
                                                + machineToCreate.getSerialNumber())
                                .header("Authorization", "Bearer " + jwToken))
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
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
        void editMaintenanceWhenExist() throws Exception {
                MaintenanceToEdit maintenanceToEdit = new MaintenanceToEdit(
                                "newTitle",
                                "newDescription");
                MachineToCreate machineToCreate = new MachineToCreate(123L,
                                "model",
                                "category",
                                "location");
                machineService.createMachine(machineToCreate);
                MaintenanceToCreate maintenanceToCreate = new MaintenanceToCreate(
                                "title",
                                "description",
                                123L);
                mockMvc.perform(MockMvcRequestBuilders
                                .post("/api/v1/maintenance-records")
                                .header("Authorization", "Bearer " + jwToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(maintenanceToCreate)))
                                .andExpect(status().isCreated());
                Long maintenanceId = maintenanceService.getAllMaintenance().get(0).getId();
                mockMvc.perform(MockMvcRequestBuilders
                                .post("/api/v1/maintenance-records/" + maintenanceId)
                                .header("Authorization", "Bearer " + jwToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(maintenanceToEdit)))
                                .andExpect(status().isOk());

                List<MaintenanceToGet> result = maintenanceService.getMaintenanceByMachine(123L);
                assertEquals(1, result.size());
                assertEquals("newTitle", result.get(0).getTitle());
                assertEquals("newDescription", result.get(0).getDescription());
                assertEquals(123L, result.get(0).getMachineId());
                assertEquals("firstname lastname", result.get(0).getTechnicianName());
        }

        @Test
        void editMaintenanceWhenDoesNotExist() throws Exception {
                MaintenanceToEdit maintenanceToEdit = new MaintenanceToEdit(
                                "newTitle",
                                "newDescription");
                long idToEdit = 3L;

                mockMvc.perform(MockMvcRequestBuilders
                                .post("/api/v1/maintenance-records/" + idToEdit)
                                .header("Authorization", "Bearer " + jwToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(maintenanceToEdit)))
                                .andExpect(status().isNotFound());

        }

        @Test
        void deleteMaintenance() throws Exception {
                MachineToCreate machineToCreate = new MachineToCreate(123L,
                                "model",
                                "category",
                                "location");
                machineService.createMachine(machineToCreate);

                MaintenanceToCreate maintenanceToCreate = new MaintenanceToCreate(
                                "title",
                                "description",
                                123L);

                mockMvc.perform(MockMvcRequestBuilders
                                .post("/api/v1/maintenance-records")
                                .header("Authorization", "Bearer " + jwToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(maintenanceToCreate)))
                                .andExpect(status().isCreated());
                Long maintenanceId = maintenanceService.getAllMaintenance().get(0).getId();

                mockMvc.perform(MockMvcRequestBuilders
                                .delete("/api/v1/maintenance-records/" + maintenanceId)
                                .header("Authorization", "Bearer " + jwToken))
                                .andExpect(status().isNoContent());
        }

        @Test
        void deleteMaintenanceWhenDoesNotExist() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders
                                .delete("/api/v1/maintenance-records/" + 1L)
                                .header("Authorization", "Bearer " + jwToken))
                                .andExpect(status().isNotFound());

        }
}
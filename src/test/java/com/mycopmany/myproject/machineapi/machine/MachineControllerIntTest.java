package com.mycopmany.myproject.machineapi.machine;

import com.mycopmany.myproject.machineapi.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MachineControllerIntTest extends AbstractIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MachineService machineService;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void getMachines() throws Exception {
        MachineToCreate machineToCreate = new MachineToCreate(123L,
                "303E CR",
                "Mini excavator",
                "Storage");
        machineService.createMachine(machineToCreate);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/machines")
                        .header("Authorization", "Bearer " + "token"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].model").value("303E CR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].category")
                        .value("Mini excavator"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].location")
                        .value("Storage"));
    }

    @Test
    void createMachineWhenIdDoesNotExist() throws Exception {
        MachineToCreate machineToCreate = new MachineToCreate(123L,
                "303E CR",
                "Mini excavator",
                "Storage");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/machines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineToCreate))
                        .header("Authorization", "Bearer " + "token"))
                .andExpect(status().isCreated());
    }

    @Test
    void createMachineWhenExists() throws Exception {
        MachineToCreate machineToCreate = new MachineToCreate(123L,
                "303E CR",
                "Mini excavator",
                "Storage");
        machineService.createMachine(machineToCreate);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/machines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineToCreate))
                        .header("Authorization", "Bearer " + "token"))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteMachineWhenIdExists() throws Exception {
        MachineToCreate machineToCreate = new MachineToCreate(123L,
                "303E CR",
                "Mini excavator",
                "Storage");
        machineService.createMachine(machineToCreate);
        Long serialNumberToDelete = 123L;
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/machines/{serialNumber}", serialNumberToDelete)
                        .header("Authorization", "Bearer " + "token"))
                .andExpect(status().isNoContent());
        List<MachineToGet> machineList = machineService.getMachines();
        assertTrue(machineList.isEmpty());
    }

    @Test
    void deleteMachineWhenDoesNotExist() throws Exception {
        Long serialNumberToDelete = 123L;
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/machines/{serialNumber}", serialNumberToDelete)
                        .header("Authorization", "Bearer " + "token"))
                .andExpect(status().isNoContent());
        List<MachineToGet> machineList = machineService.getMachines();
        assertTrue(machineList.isEmpty());
    }


    @Test
    void editMachine() throws Exception {
        MachineToCreate machineToCreate = new MachineToCreate(123L,
                "303E CR",
                "Mini excavator",
                "Storage");
        MachineToEdit machineToEdit = new MachineToEdit("New model",
                "New category",
                "New location");
        machineService.createMachine(machineToCreate);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/machines/{serialNumber}", machineToCreate.getSerialNumber())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineToEdit))
                        .header("Authorization", "Bearer " + "token"))
                .andExpect(status().isOk());

        Optional<MachineToGet> machineInDatabase = machineService.getMachines().stream()
                .filter(machine -> machine.getSerialNumber().equals(machineToCreate.getSerialNumber()))
                .findAny();

        assertTrue(machineInDatabase.isPresent());
        assertEquals("New model", machineInDatabase.get().getModel());
        assertEquals("New category", machineInDatabase.get().getCategory());
        assertEquals("New location", machineInDatabase.get().getLocation());
    }


    @Test
    void editInvalidMachine() throws Exception {
        MachineToEdit machineToEdit = new MachineToEdit("New model",
                "New category",
                "New location");

        Long serialNumberToEdit = 15L;
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/machines/{serialNumber}", serialNumberToEdit)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(machineToEdit))
                        .header("Authorization", "Bearer " + "token"))
                .andExpect(status().isNotFound());
    }
}
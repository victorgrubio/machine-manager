package com.mycopmany.myproject.machineapi.machine;

import com.mycopmany.myproject.machineapi.exception.ConflictException;
import com.mycopmany.myproject.machineapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MachineServiceTest {

    @InjectMocks
    private MachineService machineService;
    @Mock
    private MachineRepository machineRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMachines() {
        Machine machine = new Machine(123L,
                "303E CR",
                "Mini excavator",
                "Storage");
        List<Machine> mockMachines = new ArrayList<>();
        mockMachines.add(machine);
        when(machineRepository.findAll()).thenReturn(mockMachines);

        List<Machine> result = machineService.getMachines();

        verify(machineRepository).findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(123L, result.get(0).getSerialNumber());
        assertEquals("303E CR", result.get(0).getModel());
        assertEquals("Mini excavator", result.get(0).getCategory());
        assertEquals("Storage", result.get(0).getLocation());
    }

    @Test
    void createMachineWhenIdDoesNotExist() {
        MachineToCreate machineToCreate = new MachineToCreate(123L,
                "303E CR",
                "Mini excavator",
                "Storage");
        Machine testMachine = new Machine(machineToCreate.getSerialNumber(),
                machineToCreate.getModel(),
                machineToCreate.getCategory(),
                machineToCreate.getLocation());
        when(machineRepository.existsById(123L)).thenReturn(false);

        assertDoesNotThrow(() -> machineService.createMachine(machineToCreate));

        verify(machineRepository,times(1)).save(testMachine);
    }

    @Test
    void createMachineWhenIdExists() {
        MachineToCreate machineToCreate = new MachineToCreate(123L,
                "303E CR",
                "Mini excavator",
                "Storage");
        Machine machine = new Machine(machineToCreate.getSerialNumber(),
                machineToCreate.getModel(),
                machineToCreate.getCategory(),
                machineToCreate.getLocation());
        when(machineRepository.existsById(123L)).thenReturn(true);

        assertThrows(ConflictException.class,() -> machineService.createMachine(machineToCreate));

        verify(machineRepository,times(0)).save(machine);
    }

    @Test
    void deleteMachineWhenIdExists() {
        when(machineRepository.existsById(123L)).thenReturn(true);

        assertDoesNotThrow(() -> machineService.deleteMachine(123L));

        verify(machineRepository, times(1)).deleteById(123L);
    }


    @Test
    void deleteMachineWhenIdDoesNotExist() {
        when(machineRepository.existsById(123L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,() -> machineService.deleteMachine(123L));

        verify(machineRepository, times(0)).deleteById(123L);
    }

    @Test
    void updateMachineWhenIdExists() {
        Machine machine = new Machine(123L,
                "303E CR",
                "Mini excavator",
                "Storage");
        MachineToEdit machineToEdit = new MachineToEdit("New machine",
                "New category",
                "New location");
        when(machineRepository.findById(123L)).thenReturn(Optional.of(machine));

        assertDoesNotThrow(() -> machineService
                .updateMachine(123L,machineToEdit));

        assertEquals(123L, machine.getSerialNumber());
        assertEquals("New machine",machine.getModel());
        assertEquals("New category",machine.getCategory());
        assertEquals("New location",machine.getLocation());
    }

    @Test
    void updateMachineWhenIdDoesNotExist() {
        MachineToEdit machineToEdit = new MachineToEdit("New machine",
                "New category",
                "New location");
        when(machineRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,() -> machineService
                .updateMachine(123L, machineToEdit));
    }

    @Test
    void updateMachineWhenModelIsNull() {
        Machine machine = new Machine(123L,
                "303E CR",
                "Mini excavator",
                "Storage");
        MachineToEdit machineToEdit = new MachineToEdit(null,
                "New category",
                "New location");
        when(machineRepository.findById(123L)).thenReturn(Optional.of(machine));

        assertDoesNotThrow(() -> machineService
                .updateMachine(123L, machineToEdit));

        assertEquals(123L, machine.getSerialNumber());
        assertEquals("303E CR",machine.getModel());
        assertEquals("New category",machine.getCategory());
        assertEquals("New location",machine.getLocation());

    }

    @Test
    void updateMachineWhenCategoryIsNull() {
        Machine machine = new Machine(123L,
                "303E CR",
                "Mini excavator",
                "Storage");
        MachineToEdit machineToEdit = new MachineToEdit("New machine",
                null,
                "New location");
        when(machineRepository.findById(123L)).thenReturn(Optional.of(machine));

        assertDoesNotThrow(() -> machineService
                .updateMachine(123L, machineToEdit));

        assertEquals(123L, machine.getSerialNumber());
        assertEquals("New machine",machine.getModel());
        assertEquals("Mini excavator",machine.getCategory());
        assertEquals("New location",machine.getLocation());
    }

    @Test
    void updateMachineWhenLocationIsNull() {
        Machine machine = new Machine(123L,
                "303E CR",
                "Mini excavator",
                "Storage");
        MachineToEdit machineToEdit = new MachineToEdit("New machine",
                "New category",
                null);
        when(machineRepository.findById(123L)).thenReturn(Optional.of(machine));

        assertDoesNotThrow(() -> machineService
                .updateMachine(123L, machineToEdit));

        assertEquals(123L, machine.getSerialNumber());
        assertEquals("New machine",machine.getModel());
        assertEquals("New category",machine.getCategory());
        assertEquals("Storage",machine.getLocation());
    }

}
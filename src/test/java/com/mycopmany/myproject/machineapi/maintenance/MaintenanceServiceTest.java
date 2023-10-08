package com.mycopmany.myproject.machineapi.maintenance;

import com.mycopmany.myproject.machineapi.exception.ResourceNotFoundException;
import com.mycopmany.myproject.machineapi.exception.UnprocessableEntityException;
import com.mycopmany.myproject.machineapi.machine.Machine;
import com.mycopmany.myproject.machineapi.machine.MachineRepository;
import com.mycopmany.myproject.machineapi.user.AuthenticatedUser;
import com.mycopmany.myproject.machineapi.user.Role;
import com.mycopmany.myproject.machineapi.user.User;
import com.mycopmany.myproject.machineapi.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MaintenanceServiceTest {
    @InjectMocks
    private MaintenanceService maintenanceService;
    @Mock
    private MaintenanceRepository maintenanceRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private MachineRepository machineRepository;
    private User user;
    private Machine machine;
    private MaintenanceRecord maintenanceRecord;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("firstname", "lastname", "username", "password", Role.USER);
        machine = new Machine(123L, "model", "category", "location");
        maintenanceRecord = new MaintenanceRecord("title", "description", user, machine);
    }

    @Test
    void getRecords() {
        LocalDateTime maintenanceDate = LocalDateTime.now();
        maintenanceRecord.setMaintenanceDate(maintenanceDate);
        List<MaintenanceRecord> mockMaintenance = new ArrayList<>();
        mockMaintenance.add(maintenanceRecord);
        when(maintenanceRepository.findAll()).thenReturn(mockMaintenance);

        List<MaintenanceRecordToGet> result = maintenanceService.getRecords();

        verify(maintenanceRepository).findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("title", result.get(0).getTitle());
        assertEquals("description", result.get(0).getDescription());
        assertEquals("firstname lastname", result.get(0).getTechnicianName());
        assertEquals(123L, result.get(0).getMachineId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");
        String formattedMaintenanceDate = maintenanceDate.format(formatter);
        assertEquals(formattedMaintenanceDate, result.get(0).getMaintenanceDate());

    }

    @Test
    void getRecordsByMachine() {
        LocalDateTime maintenanceDate = LocalDateTime.now();
        maintenanceRecord.setMaintenanceDate(maintenanceDate);
        List<MaintenanceRecord> mockMaintenance = new ArrayList<>();
        mockMaintenance.add(maintenanceRecord);
        when(machineRepository.existsById(123L)).thenReturn(true);
        when(maintenanceRepository.findByMachineSerialNumber(123L)).thenReturn(mockMaintenance);
        List<MaintenanceRecordToGet> result = maintenanceService.getRecordsByMachine(123L);

        verify(maintenanceRepository).findByMachineSerialNumber(123L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("title", result.get(0).getTitle());
        assertEquals("description", result.get(0).getDescription());
        assertEquals("firstname lastname", result.get(0).getTechnicianName());
        assertEquals(123L, result.get(0).getMachineId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");
        String formattedMaintenanceDate = maintenanceDate.format(formatter);
        assertEquals(formattedMaintenanceDate, result.get(0).getMaintenanceDate());

    }

    @Test
    void getRecordsByMachineWhenMachineNotExist() {
        when(machineRepository.existsById(123L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> maintenanceService.getRecordsByMachine(123L));

        verify(machineRepository).existsById(123L);
        verify(maintenanceRepository,times(0)).findByMachineSerialNumber(123L);
    }

    @Test
    void createRecord() {
        MaintenanceRecordToCreate maintenanceRecordToCreate = new MaintenanceRecordToCreate(
                "title",
                "description",
                123L
        );
        maintenanceRecord.setMaintenanceDate(LocalDateTime.now());
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(authenticatedUser);
        when(userRepository.findByUsername(authenticatedUser.getUsername())).thenReturn(Optional.of(user));
        when(machineRepository.findById(machine.getSerialNumber())).thenReturn(Optional.of(machine));

        maintenanceService.createRecord(maintenanceRecordToCreate);

        verify(maintenanceRepository,times(1)).save(maintenanceRecord);

    }
    @Test
    void createRecordWhenMachineNotFound(){
        MaintenanceRecordToCreate maintenanceRecordToCreate = new MaintenanceRecordToCreate(
                "title",
                "description",
                123L
        );
        maintenanceRecord.setMaintenanceDate(LocalDateTime.now());
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(authenticatedUser);
        when(userRepository.findByUsername(authenticatedUser.getUsername())).thenReturn(Optional.of(user));

        assertThrows(ResourceNotFoundException.class,
                () -> maintenanceService.createRecord(maintenanceRecordToCreate));
        verify(maintenanceRepository,times(0)).save(maintenanceRecord);

    }
    @Test
    void createRecordWhenEmptyTitle() {
        MaintenanceRecordToCreate maintenanceRecordToCreate = new MaintenanceRecordToCreate(
                "",
                "description",
                123L
        );
        maintenanceRecord.setMaintenanceDate(LocalDateTime.now());
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(authenticatedUser);
        when(userRepository.findByUsername(authenticatedUser.getUsername())).thenReturn(Optional.of(user));
        when(machineRepository.findById(machine.getSerialNumber())).thenReturn(Optional.of(machine));

        assertThrows(UnprocessableEntityException.class,
                () ->maintenanceService.createRecord(maintenanceRecordToCreate));
        verify(maintenanceRepository, times(0)).save(maintenanceRecord);

    }



    @Test
    void editRecordWhenExists() {
        MaintenanceRecordToEdit maintenanceRecordToEdit = new MaintenanceRecordToEdit(
                "newTitle",
                "newDescription"
        );
        Long idToEdit = 3L;
        when(maintenanceRepository.findById(idToEdit)).thenReturn(Optional.of(maintenanceRecord));

        assertDoesNotThrow(() -> maintenanceService
                .editRecord(3L,maintenanceRecordToEdit));

        assertEquals("newTitle", maintenanceRecord.getTitle());
        assertEquals("newDescription", maintenanceRecord.getDescription());
    }
    @Test
    void editRecordWhenDoesNotExist() {
        MaintenanceRecordToEdit maintenanceRecordToEdit = new MaintenanceRecordToEdit(
                "newTitle",
                "new description"
        );
        Long idToEdit = 3L;

        assertThrows(ResourceNotFoundException.class,() -> maintenanceService
                .editRecord(3L,maintenanceRecordToEdit));

        assertEquals("title", maintenanceRecord.getTitle());
        assertEquals("description", maintenanceRecord.getDescription());
    }
    @Test
    void deleteRecordWhenIdExists() {
        when(maintenanceRepository.existsById(123L)).thenReturn(true);

        assertDoesNotThrow(() -> maintenanceService.deleteRecord(123L));

        verify(maintenanceRepository, times(1)).deleteById(123L);

    }

    @Test
    void deleteRecordWhenIdDoesNotExist() {
        when(maintenanceRepository.existsById(123L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,() -> maintenanceService.deleteRecord(123L));

        verify(maintenanceRepository, times(0)).deleteById(123L);

    }
}
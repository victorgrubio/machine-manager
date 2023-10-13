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
    private Maintenance maintenance;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("firstname", "lastname", "username", "password", Role.USER);
        machine = new Machine(123L, "model", "category", "location");
        maintenance = new Maintenance("title", "description", user, machine);
    }

    @Test
    void getRecords() {
        LocalDateTime maintenanceDate = LocalDateTime.now();
        maintenance.setMaintenanceDate(maintenanceDate);
        List<Maintenance> mockMaintenance = new ArrayList<>();
        mockMaintenance.add(maintenance);
        when(maintenanceRepository.findAll()).thenReturn(mockMaintenance);

        List<MaintenanceToGet> result = maintenanceService.getAllMaintenance();

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
        maintenance.setMaintenanceDate(maintenanceDate);
        List<Maintenance> mockMaintenance = new ArrayList<>();
        mockMaintenance.add(maintenance);
        when(machineRepository.existsById(123L)).thenReturn(true);
        when(maintenanceRepository.findByMachineSerialNumber(123L)).thenReturn(mockMaintenance);
        List<MaintenanceToGet> result = maintenanceService.getMaintenanceByMachine(123L);

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

        assertThrows(ResourceNotFoundException.class, () -> maintenanceService.getMaintenanceByMachine(123L));

        verify(machineRepository).existsById(123L);
        verify(maintenanceRepository,times(0)).findByMachineSerialNumber(123L);
    }

    @Test
    void createMaintenance() {
        MaintenanceToCreate maintenanceToCreate = new MaintenanceToCreate(
                "title",
                "description",
                123L
        );
        maintenance.setMaintenanceDate(LocalDateTime.now());
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(authenticatedUser);
        when(userRepository.findByUsername(authenticatedUser.getUsername())).thenReturn(Optional.of(user));
        when(machineRepository.findById(machine.getSerialNumber())).thenReturn(Optional.of(machine));

        maintenanceService.createMaintenance(maintenanceToCreate);

        verify(maintenanceRepository,times(1)).save(maintenance);

    }
    @Test
    void createMaintenanceWhenMachineNotFound(){
        MaintenanceToCreate maintenanceToCreate = new MaintenanceToCreate(
                "title",
                "description",
                123L
        );
        maintenance.setMaintenanceDate(LocalDateTime.now());
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(authenticatedUser);
        when(userRepository.findByUsername(authenticatedUser.getUsername())).thenReturn(Optional.of(user));

        assertThrows(ResourceNotFoundException.class,
                () -> maintenanceService.createMaintenance(maintenanceToCreate));
        verify(maintenanceRepository,times(0)).save(maintenance);

    }
    @Test
    void createMaintenanceWhenEmptyTitle() {
        MaintenanceToCreate maintenanceToCreate = new MaintenanceToCreate(
                "",
                "description",
                123L
        );
        maintenance.setMaintenanceDate(LocalDateTime.now());
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(authenticatedUser);
        when(userRepository.findByUsername(authenticatedUser.getUsername())).thenReturn(Optional.of(user));
        when(machineRepository.findById(machine.getSerialNumber())).thenReturn(Optional.of(machine));

        assertThrows(UnprocessableEntityException.class,
                () ->maintenanceService.createMaintenance(maintenanceToCreate));
        verify(maintenanceRepository, times(0)).save(maintenance);

    }



    @Test
    void editMaintenanceWhenExists() {
        MaintenanceToEdit maintenanceToEdit = new MaintenanceToEdit(
                "newTitle",
                "newDescription"
        );
        Long idToEdit = 3L;
        when(maintenanceRepository.findById(idToEdit)).thenReturn(Optional.of(maintenance));

        assertDoesNotThrow(() -> maintenanceService
                .editMaintenance(3L, maintenanceToEdit));

        assertEquals("newTitle", maintenance.getTitle());
        assertEquals("newDescription", maintenance.getDescription());
    }
    @Test
    void editMaintenanceWhenDoesNotExist() {
        MaintenanceToEdit maintenanceToEdit = new MaintenanceToEdit(
                "newTitle",
                "new description"
        );
        Long idToEdit = 3L;

        assertThrows(ResourceNotFoundException.class,() -> maintenanceService
                .editMaintenance(3L, maintenanceToEdit));

        assertEquals("title", maintenance.getTitle());
        assertEquals("description", maintenance.getDescription());
    }
    @Test
    void deleteMaintenanceWhenIdExists() {
        when(maintenanceRepository.existsById(123L)).thenReturn(true);

        assertDoesNotThrow(() -> maintenanceService.deleteMaintenance(123L));

        verify(maintenanceRepository, times(1)).deleteById(123L);

    }

    @Test
    void deleteMaintenanceWhenIdDoesNotExist() {
        when(maintenanceRepository.existsById(123L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,() -> maintenanceService.deleteMaintenance(123L));

        verify(maintenanceRepository, times(0)).deleteById(123L);

    }
}
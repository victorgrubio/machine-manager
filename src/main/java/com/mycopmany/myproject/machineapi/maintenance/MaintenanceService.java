package com.mycopmany.myproject.machineapi.maintenance;

import com.mycopmany.myproject.machineapi.exception.ResourceNotFoundException;
import com.mycopmany.myproject.machineapi.exception.UnprocessableEntityException;
import com.mycopmany.myproject.machineapi.machine.Machine;
import com.mycopmany.myproject.machineapi.machine.MachineRepository;
import com.mycopmany.myproject.machineapi.user.AuthenticatedUser;
import com.mycopmany.myproject.machineapi.user.User;
import com.mycopmany.myproject.machineapi.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MaintenanceService {
    private final MaintenanceRepository maintenanceRepository;
    private final UserRepository userRepository;
    private final MachineRepository machineRepository;


    public List<MaintenanceToGet> getAllMaintenance(){
        return maintenanceRepository.findAll()
                .stream()
                .map(MaintenanceToGet::fromModel)
                .collect(Collectors.toList());
    }
    public List<MaintenanceToGet> getMaintenanceByMachine(Long serialNumber){
        boolean machineExists = machineRepository.existsById(serialNumber);
        if (!machineExists)
            throw new ResourceNotFoundException("Machine does not exist");
        return maintenanceRepository.findByMachineSerialNumber(serialNumber)
                .stream()
                .map(MaintenanceToGet::fromModel).
                collect(Collectors.toList());
    }
    public void createMaintenance(MaintenanceToCreate maintenanceToCreate){
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        Machine machine = machineRepository.findById(maintenanceToCreate.getMachineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found"));

        if (maintenanceToCreate.getTitle() == null ||
                maintenanceToCreate.getTitle().trim().isEmpty())
            throw new UnprocessableEntityException("Invalid title");

        Maintenance maintenance = new Maintenance(
                maintenanceToCreate.getTitle(),
                maintenanceToCreate.getDescription(),
                user,
                machine);
        maintenanceRepository.save(maintenance);
    }

    @Transactional
    public void editMaintenance(Long recordId, MaintenanceToEdit maintenanceToEdit){
        Maintenance maintenance = maintenanceRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Maintenance record with id: " + recordId + " does not exist"));
        if (maintenanceToEdit.getTitle() != null &&
                maintenanceToEdit.getTitle().trim().length() > 0){
            maintenance.setTitle(maintenanceToEdit.getTitle());
        }
        maintenance.setDescription(maintenanceToEdit.getDescription());

    }
    public void deleteMaintenance(Long recordId){
        boolean recordExists = maintenanceRepository.existsById(recordId);
        if (recordExists)
            maintenanceRepository.deleteById(recordId);
        else
            throw new ResourceNotFoundException("record does not exist");
    }
}

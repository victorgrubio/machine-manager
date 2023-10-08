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


    public List<MaintenanceRecordToGet> getRecords(){
        return maintenanceRepository.findAll()
                .stream()
                .map(MaintenanceRecordToGet::fromModel)
                .collect(Collectors.toList());
    }
    public List<MaintenanceRecordToGet> getRecordsByMachine(Long serialNumber){
        boolean machineExists = machineRepository.existsById(serialNumber);
        if (!machineExists)
            throw new ResourceNotFoundException("Machine does not exist");
        return maintenanceRepository.findByMachineSerialNumber(serialNumber)
                .stream()
                .map(MaintenanceRecordToGet::fromModel).
                collect(Collectors.toList());
    }
    public void createRecord(MaintenanceRecordToCreate maintenanceRecordToCreate){
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        Machine machine = machineRepository.findById(maintenanceRecordToCreate.getMachineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found"));

        if (maintenanceRecordToCreate.getTitle() == null ||
                maintenanceRecordToCreate.getTitle().trim().isEmpty())
            throw new UnprocessableEntityException("Invalid title");

        MaintenanceRecord maintenanceRecord = new MaintenanceRecord(
                maintenanceRecordToCreate.getTitle(),
                maintenanceRecordToCreate.getDescription(),
                user,
                machine);
        maintenanceRepository.save(maintenanceRecord);
    }

    @Transactional
    public void editRecord(Long recordId, MaintenanceRecordToEdit maintenanceRecordToEdit){
        MaintenanceRecord maintenanceRecord = maintenanceRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Maintenance record with id: " + recordId + " does not exist"));
        if (maintenanceRecordToEdit.getTitle() != null &&
                maintenanceRecordToEdit.getTitle().trim().length() > 0){
            maintenanceRecord.setTitle(maintenanceRecordToEdit.getTitle());
        }
        maintenanceRecord.setDescription(maintenanceRecordToEdit.getDescription());

    }
    public void deleteRecord(Long recordId){
        boolean recordExists = maintenanceRepository.existsById(recordId);
        if (recordExists)
            maintenanceRepository.deleteById(recordId);
        else
            throw new ResourceNotFoundException("record does not exist");
    }
}

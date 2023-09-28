package com.mycopmany.myproject.machineapi.machine;

import com.mycopmany.myproject.machineapi.exception.ConflictException;
import com.mycopmany.myproject.machineapi.exception.ResourceNotFoundException;
import com.mycopmany.myproject.machineapi.exception.UnprocessableEntityException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
public class MachineService {
    private MachineRepository machineRepository;

    public List<Machine> getMachines(){
         return machineRepository.findAll();
    }

    public void createMachine(MachineToCreate machineToCreate){
        validateMachineToCreate(machineToCreate);
        Machine machine = new Machine(
                machineToCreate.getSerialNumber(),
                machineToCreate.getModel(),
                machineToCreate.getCategory(),
                machineToCreate.getLocation());
        machineRepository.save(machine);
    }
    public void deleteMachine(Long serialNumber){
        machineRepository.deleteById(serialNumber);
    }

    @Transactional
    public void updateMachine(Long serialNumber, MachineToEdit machineToEdit){
        Machine machine = machineRepository.findById(serialNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Machine with serialNumber: " + serialNumber + " does not exist"));
        if (machineToEdit.getModel() != null &&
                machineToEdit.getModel().trim().length() > 0 &&
                !Objects.equals(machine.getModel(), machineToEdit.getModel())){
            machine.setModel(machineToEdit.getModel());
        }
        if (machineToEdit.getCategory() != null &&
                machineToEdit.getCategory().trim().length() > 0 &&
                !Objects.equals(machine.getCategory(), machineToEdit.getCategory())){
            machine.setCategory(machineToEdit.getCategory());
        }
        if (machineToEdit.getLocation() != null &&
                machineToEdit.getLocation().trim().length() > 0 &&
                !Objects.equals(machine.getLocation(), machineToEdit.getLocation())){
            machine.setLocation(machineToEdit.getLocation());
        }
    }
    private void validateMachineToCreate(MachineToCreate machineToCreate){
        boolean machineExists = machineRepository.existsById(machineToCreate.getSerialNumber());
        if (machineExists)
            throw new ConflictException("Machine already exists");

        if (machineToCreate.getSerialNumber() == null ||
                machineToCreate.getSerialNumber() > 0)
            throw new UnprocessableEntityException("Invalid serial number");

        if (machineToCreate.getModel() == null ||
                machineToCreate.getModel().trim().isEmpty())
            throw new UnprocessableEntityException("Invalid model name");

        if (machineToCreate.getCategory() == null ||
                machineToCreate.getCategory().trim().isEmpty())
            throw new UnprocessableEntityException("Invalid category");

        if (machineToCreate.getLocation() == null ||
                machineToCreate.getLocation().trim().isEmpty())
            throw new UnprocessableEntityException("Invalid location");
    }

}

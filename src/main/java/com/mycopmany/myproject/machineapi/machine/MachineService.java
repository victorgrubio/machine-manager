package com.mycopmany.myproject.machineapi.machine;

import com.mycopmany.myproject.machineapi.exception.ResourceNotFoundException;
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

    public void createMachine(CreateMachine createMachine){
        Machine machine = new Machine(
                createMachine.getSerialNumber(),
                createMachine.getModel(),
                createMachine.getCategory(),
                createMachine.getLocation()
        );
        machineRepository.save(machine);
    }
    public void deleteMachine(Long serialNumber){
        machineRepository.deleteById(serialNumber);
    }

    @Transactional
    public void updateMachine(Long serialNumber,EditMachine editMachine){
        Machine machine = machineRepository.findById(serialNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Machine with serialNumber: " + serialNumber + " does not exist"));
        if (editMachine.getModel() != null &&
                editMachine.getModel().trim().length() > 0 &&
                !Objects.equals(machine.getModel(),editMachine.getModel())){
            machine.setModel(editMachine.getModel());
        }
        if (editMachine.getCategory() != null &&
                editMachine.getCategory().trim().length() > 0 &&
                !Objects.equals(machine.getCategory(),editMachine.getCategory())){
            machine.setCategory(editMachine.getCategory());
        }
        if (editMachine.getLocation() != null &&
                editMachine.getLocation().trim().length() > 0 &&
                !Objects.equals(machine.getLocation(),editMachine.getLocation())){
            machine.setLocation(editMachine.getLocation());
        }

    }

}

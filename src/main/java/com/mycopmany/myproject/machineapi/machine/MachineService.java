package com.mycopmany.myproject.machineapi.machine;

import com.mycopmany.myproject.machineapi.exception.ResourceNotFoundException;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MachineService {
    private final MachineRepository machineRepository;

    @Transactional(readOnly = true)
    public List<MachineToGet> getMachines(){
         return machineRepository.findAll()
                 .stream()
                 .map(MachineToGet::fromModel)
                 .collect(Collectors.toList());
    }

    @Transactional
    public void createMachine(MachineToCreate machineToCreate){
        Machine machine = new Machine(
                machineToCreate.getSerialNumber(),
                machineToCreate.getModel(),
                machineToCreate.getCategory(),
                machineToCreate.getLocation());
        machineRepository.save(machine);
    }
    @Transactional
    public void deleteMachine(Long serialNumber){
        machineRepository.deleteBySerialNumber(serialNumber);
    }

    @Transactional
    public void updateMachine(Long serialNumber, MachineToEdit machineToEdit){
        Machine machine = machineRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Machine with serialNumber: " + serialNumber + " does not exist"));
        if (StringUtils.isNotBlank(machineToEdit.getModel())) {
            machine.setModel(machineToEdit.getModel());
        }

        if (StringUtils.isNotBlank(machineToEdit.getCategory())) {
            machine.setCategory(machineToEdit.getCategory());
        }

        if (StringUtils.isNotBlank(machineToEdit.getLocation())) {
            machine.setLocation(machineToEdit.getLocation());
        }
    }


}

package com.mycopmany.myproject.machineapi.machine;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "api/v1/machines")
public class MachineController {
    private final MachineService machineService;

    @GetMapping
    public List<MachineToGet> getMachines() {
        return machineService.getMachines();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createMachine(@RequestBody MachineToCreate machineToCreate) {
        machineService.createMachine(machineToCreate);
    }
    @DeleteMapping(path = "{serialNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMachine(@PathVariable("serialNumber") Long serialNumber){
        machineService.deleteMachine(serialNumber);
    }
    @PostMapping(path = "{serialNumber}")
    public void editMachine(@PathVariable("serialNumber") Long serialNumber, @RequestBody MachineToEdit machineToEdit){
        machineService.updateMachine(serialNumber, machineToEdit);
    }

}

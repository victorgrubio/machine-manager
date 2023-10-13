package com.mycopmany.myproject.machineapi.maintenance;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "api/v1/maintenance-records")
public class MaintenanceController {
    private final MaintenanceService maintenanceService;
    @GetMapping
    public List<MaintenanceToGet> getMaintenance() {
        return maintenanceService.getAllMaintenance();
    }

    @GetMapping("/by-machine/{serialNumber}")
    public List<MaintenanceToGet> getMaintenanceBySerialNumber(@PathVariable Long serialNumber) {
        return maintenanceService.getMaintenanceByMachine(serialNumber);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createMaintenance(@RequestBody MaintenanceToCreate maintenanceToCreate){
        maintenanceService.createMaintenance(maintenanceToCreate);
    }

    @PostMapping(path = "{id}")
    public void editMaintenance(@PathVariable Long Id,@RequestBody MaintenanceToEdit maintenanceToEdit){
        maintenanceService.editMaintenance(Id, maintenanceToEdit);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMaintenance(@PathVariable("Id") Long Id){
        maintenanceService.deleteMaintenance(Id);
    }
}

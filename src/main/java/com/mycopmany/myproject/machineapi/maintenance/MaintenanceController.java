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
    public List<MaintenanceRecordToGet> getRecords() {
        return maintenanceService.getRecords();
    }

    @GetMapping("/by-machine/{serialNumber}")
    public List<MaintenanceRecordToGet> getMaintenanceRecordsBySerialNumber(@PathVariable Long serialNumber) {
        return maintenanceService.getRecordsByMachine(serialNumber);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createRecord(@RequestBody MaintenanceRecordToCreate maintenanceRecordToCreate){
        maintenanceService.createRecord(maintenanceRecordToCreate);
    }

    @PostMapping(path = "{recordId}")
    public void editRecord(@PathVariable Long recordId,@RequestBody MaintenanceRecordToEdit maintenanceRecordToEdit){
        maintenanceService.editRecord(recordId, maintenanceRecordToEdit);
    }

    @DeleteMapping(path = "{recordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecord(@PathVariable("recordId") Long recordId){
        maintenanceService.deleteRecord(recordId);
    }
}

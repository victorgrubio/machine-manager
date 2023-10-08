package com.mycopmany.myproject.machineapi.maintenance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
public class MaintenanceRecordToGet {
    private Long id;
    private String maintenanceDate;
    private String title;
    private String description;
    private String technicianName;
    private Long machineId;

    public static MaintenanceRecordToGet fromModel(MaintenanceRecord maintenanceRecord){
        String technicianName = String.format("%s %s",
                maintenanceRecord.getUser().getFirstName(),
                maintenanceRecord.getUser().getLastName());
        LocalDateTime maintenanceDate = maintenanceRecord.getMaintenanceDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");
        String formattedMaintenanceDate = maintenanceDate.format(formatter);


        return new MaintenanceRecordToGet(
                maintenanceRecord.getId(),
                formattedMaintenanceDate,
                maintenanceRecord.getTitle(),
                maintenanceRecord.getDescription(),
                technicianName,
                maintenanceRecord.getMachine().getSerialNumber()
        );
    }


}

package com.mycopmany.myproject.machineapi.maintenance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
public class MaintenanceToGet {
    private Long id;
    private String maintenanceDate;
    private String title;
    private String description;
    private String technicianName;
    private Long machineId;

    public static MaintenanceToGet fromModel(Maintenance maintenance){
        String technicianName = String.format("%s %s",
                maintenance.getUser().getFirstName(),
                maintenance.getUser().getLastName());
        LocalDateTime maintenanceDate = maintenance.getMaintenanceDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");
        String formattedMaintenanceDate = maintenanceDate.format(formatter);


        return new MaintenanceToGet(
                maintenance.getId(),
                formattedMaintenanceDate,
                maintenance.getTitle(),
                maintenance.getDescription(),
                technicianName,
                maintenance.getMachine().getSerialNumber()
        );
    }


}

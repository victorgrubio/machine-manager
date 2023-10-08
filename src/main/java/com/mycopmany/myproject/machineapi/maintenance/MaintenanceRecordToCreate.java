package com.mycopmany.myproject.machineapi.maintenance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MaintenanceRecordToCreate {
    private String title;
    private String description;
    private Long machineId;

}

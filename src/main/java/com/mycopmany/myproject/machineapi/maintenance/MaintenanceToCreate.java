package com.mycopmany.myproject.machineapi.maintenance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MaintenanceToCreate {
    private String title;
    private String description;
    private Long machineSerialNumber;

}

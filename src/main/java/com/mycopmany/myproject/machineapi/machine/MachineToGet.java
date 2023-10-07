package com.mycopmany.myproject.machineapi.machine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MachineToGet {
    private Long serialNumber;
    private String model;
    private String category;
    private String location;

    public static MachineToGet fromModel(Machine machine){
        return new MachineToGet(
                machine.getSerialNumber(),
                machine.getModel(),
                machine.getCategory(),
                machine.getLocation()
        );
    }
}

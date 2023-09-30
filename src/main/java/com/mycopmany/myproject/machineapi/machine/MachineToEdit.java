package com.mycopmany.myproject.machineapi.machine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MachineToEdit {
    private String model;
    private String category;
    private String location;
}

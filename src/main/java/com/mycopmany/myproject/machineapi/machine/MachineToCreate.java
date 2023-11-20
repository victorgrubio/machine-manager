package com.mycopmany.myproject.machineapi.machine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MachineToCreate {
    @Positive
    private Long serialNumber;
    @NotBlank
    private String model;
    @NotBlank
    private String category;
    @NotBlank
    private String location;
}

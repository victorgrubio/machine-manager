package com.mycopmany.myproject.machineapi.machine;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Machine {
    @Id
    private Long serialNumber;
    private String model;
    private String category;
    private String location;

}

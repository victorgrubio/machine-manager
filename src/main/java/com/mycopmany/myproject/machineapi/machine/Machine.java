package com.mycopmany.myproject.machineapi.machine;

import com.mycopmany.myproject.machineapi.maintenance.MaintenanceRecord;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;


@Getter
@Setter
@Entity
@NoArgsConstructor
public class Machine {
    @Id
    private Long serialNumber;
    private String model;
    private String category;
    private String location;
    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaintenanceRecord> maintenanceRecords;

    public Machine(Long serialNumber, String model, String category, String location) {
        this.serialNumber = serialNumber;
        this.model = model;
        this.category = category;
        this.location = location;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Machine other))
            return false;

        return Objects.equals(other.getSerialNumber(), getSerialNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber);
    }
}

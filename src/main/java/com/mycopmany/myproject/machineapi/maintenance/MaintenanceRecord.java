package com.mycopmany.myproject.machineapi.maintenance;

import com.mycopmany.myproject.machineapi.machine.Machine;
import com.mycopmany.myproject.machineapi.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class MaintenanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    private LocalDateTime maintenanceDate;
    private String title;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id")
    private Machine machine;

    public MaintenanceRecord(String title, String description, User user, Machine machine) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.machine = machine;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof MaintenanceRecord other))
            return false;

        return Objects.equals(other.getId(), getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package com.mycopmany.myproject.machineapi.maintenance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRepository extends JpaRepository<MaintenanceRecord,Long> {
    List<MaintenanceRecord> findByMachineSerialNumber(Long serialNumber);
}

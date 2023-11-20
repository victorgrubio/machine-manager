package com.mycopmany.myproject.machineapi.machine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MachineRepository extends JpaRepository<Machine, Long> {
     Optional<Machine> findBySerialNumber(Long serialNumber);
     boolean existsBySerialNumber(Long serialNumber);
     void deleteBySerialNumber(Long serialNumber);
}

package com.jwierzb.parkingspaces.dao;

import com.jwierzb.parkingspaces.entity.DriverType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverTypeDao extends JpaRepository<DriverType, Integer> {
    Optional<DriverType> findByName(String name);
}

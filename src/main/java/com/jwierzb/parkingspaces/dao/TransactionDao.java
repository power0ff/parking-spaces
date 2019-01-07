package com.jwierzb.parkingspaces.dao;

import com.jwierzb.parkingspaces.entity.Transaction;
import com.jwierzb.parkingspaces.entity.UserEntity;
import com.jwierzb.parkingspaces.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionDao extends JpaRepository<Transaction, Long> {

    /**
     * Find if there exists begone transaction
     * @param userEntity
     * @param vehicle
     * @return
     */
    Optional<Transaction> findByUserAndVehicleAndEndTimeIsNull(UserEntity userEntity, Vehicle vehicle);
    Boolean existsByUserAndVehicleAndEndTimeIsNull(UserEntity userEntity, Vehicle vehicle);
    List<Transaction> findAllByUserAndEndTimeNotNull(UserEntity userEntity);
    Optional<Transaction> findByVehicleAndEndTimeIsNull(Vehicle vehicle);
    Integer countAllByEndTimeNull();
    Integer countAllByEndTimeBetween(LocalDateTime left, LocalDateTime right);
    List<Transaction> findAllByEndTimeBetween(LocalDateTime left, LocalDateTime right);

}

package com.jwierzb.parkingspaces.dao;

import com.jwierzb.parkingspaces.entity.UserEntity;
import com.jwierzb.parkingspaces.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface VehicleDao extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findAllByUser(UserEntity userEntity);
    Optional<Vehicle> findByUserAndRegistrationNumber(UserEntity userEntity, String registrationNumber);
    Boolean existsVehicleByRegistrationNumber(String registrationNumber);
    Boolean existsVehicleByUser(UserEntity userEntity);
    Boolean existsVehicleByUserAndRegistrationNumber(UserEntity userEntity, String reg_number);
    Optional<Vehicle> findByRegistrationNumber(String reg_number);
    @Transactional
    void deleteByUserAndRegistrationNumber(UserEntity userEntity, String reg_number);

}

package com.jwierzb.parkingspaces.dao;

import com.jwierzb.parkingspaces.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserDao extends JpaRepository<UserEntity, Long> {
    UserEntity save(UserEntity userEntity);
    UserEntity findByUsername(String name);
    Boolean existsByUsername(String name);
    Boolean existsByEmail(String email);
}

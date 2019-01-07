package com.jwierzb.parkingspaces.dao;

import com.jwierzb.parkingspaces.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RoleDao extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
    List<Role> findAllByName(String name);
}

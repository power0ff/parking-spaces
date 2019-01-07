package com.jwierzb.parkingspaces.dao;

import com.jwierzb.parkingspaces.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyDao extends JpaRepository<Currency, Integer> {
    Optional<Currency> findByName(String name);
}

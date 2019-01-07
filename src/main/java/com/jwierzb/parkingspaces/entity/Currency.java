package com.jwierzb.parkingspaces.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static lombok.AccessLevel.*;

@Entity
@Data
@FieldDefaults(level = PRIVATE)
@Table(name = "CURRENCY")
@Proxy(lazy = false)
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @JsonIgnore
    Integer id;

    @NotNull
    @Column(name = "NAME")
    String name;


    /**
     * Exchange rate in terms of PLN
     */
    @NotNull
    @Column(name = "EXCHANGE_RATE")
    Double exchangeRate;


    @NotNull
    @Column(name = "LAST_UPDATE")
    LocalDateTime lastUpdate;

    @PreUpdate
    @PrePersist
    void updateLastUpdate(){lastUpdate=LocalDateTime.now();}


}

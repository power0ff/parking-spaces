package com.jwierzb.parkingspaces.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.*;

/**
 * UserEntity's vehilce class. Users can have one vehicle.
 * One vehicle can be assigned to exactly one UserEntity's account.
 * TODO let userEntity have more vehicles than one and let users share vehicles
 */

@Table(name = "VEHICLE")
@Entity
@FieldDefaults(level = PRIVATE)
@Data
@Builder
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    Long id;

    @NotNull
    @Length(max = 7)
    @Column(name = "REGISTRATION_NUMBER", unique = true)
    String registrationNumber;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    @JsonIgnore
    UserEntity user;


    @JsonGetter
    String getUsername(){return user.getUsername();}

    public Vehicle(){}
}

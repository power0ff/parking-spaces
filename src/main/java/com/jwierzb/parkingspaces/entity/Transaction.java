package com.jwierzb.parkingspaces.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.lang.Math.pow;
import static lombok.AccessLevel.*;

@Entity
@Data
@Table(name = "TRANSACTION")
@FieldDefaults(level = PRIVATE)
@Builder
@AllArgsConstructor
@Transactional
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @JsonIgnore
    Long id;

    @Column(name = "END_TIME")
    @JsonIgnore
    LocalDateTime endTime;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    UserEntity user;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    Vehicle vehicle;

    @NotNull
    @JsonIgnore
    @Column(name = "START_TIME", nullable = false, updatable = false)
    LocalDateTime startTime;

    @Column(name = "PRICE")
    @JsonIgnore
    Double price;

    @Column(name = "PAYED")
    Boolean payed;


    @JsonGetter
    String getParkingPrice()
    {
        if(price == null) return "N/A";
        return String.format("%.3f", price/ user.getCurrency().getExchangeRate()) + user.getCurrency().getName();
    }

    /**
     * Update price with value in PLN
     */
    @PreUpdate
    void setPrice()
    {
        if(endTime==null) return;

        Currency currency = user.getCurrency();
        DriverType driverType = user.getDriverType();
        long hours = ChronoUnit.HOURS.between(startTime, endTime)+1;

        if(driverType.getNextHoursMultipiler()==1) price = (driverType.getFirstHourPrice()+(hours-1)*driverType.getSecondHourPrice());
        price = (driverType.getFirstHourPrice()+driverType.getSecondHourPrice()*(1-pow(driverType.getNextHoursMultipiler(), hours-1))/(1-driverType.getNextHoursMultipiler()));

    }


    @PrePersist
    void startTime(){
        this.startTime = LocalDateTime.now();
    }

    @JsonGetter
    String getEndTime()
    {
        if(endTime==null) return "N/A";
        return endTime.toString();
    }
    @JsonGetter
    String getStartTime()
    {
        return startTime.toString();    }

    @JsonGetter
    String getUserName() { return user.getUsername();}

    @JsonGetter
    String getVehicleRegistrationNumber(){return vehicle.getRegistrationNumber();
    }
    public Transaction(){}


}

package com.jwierzb.parkingspaces.controller;

import com.jwierzb.parkingspaces.dao.*;
import com.jwierzb.parkingspaces.entity.Transaction;
import com.jwierzb.parkingspaces.entity.UserEntity;
import com.jwierzb.parkingspaces.entity.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/operator")
public class OperatorController {


    @Autowired
    UserDao users;
    @Autowired
    DriverTypeDao driverTypeDao;
    @Autowired
    CurrencyDao currencyDao;
    @Autowired
    RoleDao roleDao;
    @Autowired
    VehicleDao vehicleDao;
    @Autowired
    TransactionDao transactionDao;


    @GetMapping("/check/{reg_number}")
    String checkVehicle(Principal principal,
                        @PathVariable String reg_number)
    {
        UserEntity userEntity = users.findByUsername(principal.getName());

        Optional<Vehicle> vehicle = vehicleDao.findByRegistrationNumber(reg_number.toUpperCase());
        if(!vehicle.isPresent()) return "Vehicle is not in database";

        Optional<Transaction> transaction = transactionDao.findByVehicleAndEndTimeIsNull(vehicle.get());
        if(!transaction.isPresent()) return "Vehicle doesn't started parking meter!";

        return "Vehicle started parkometer";
    }
}

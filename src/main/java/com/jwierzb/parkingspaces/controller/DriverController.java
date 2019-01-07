package com.jwierzb.parkingspaces.controller;


import com.jwierzb.parkingspaces.dao.*;
import com.jwierzb.parkingspaces.entity.Transaction;
import com.jwierzb.parkingspaces.entity.UserEntity;
import com.jwierzb.parkingspaces.entity.Vehicle;
import com.jwierzb.parkingspaces.exception.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/driver")
@RestController
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverController {

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

    /**
     * Registering userEntity's vehicle. At this moment userEntity can have only one vehicle registered.
     * @param reg_number
     * @return
     * @throws VehicleRegistrationForbiddenException
     */
    @PostMapping("/vehicle/register/{reg_number}")
    public Vehicle newVehicle(Principal principal, @PathVariable String reg_number) throws VehicleRegistrationForbiddenException
    {
        UserEntity userEntity = users.findByUsername(principal.getName());
        reg_number = reg_number.toUpperCase();

        /**
         * One vehicle per account.
         */
        if(vehicleDao.existsVehicleByUser(userEntity)) throw new VehicleRegistrationForbiddenException("UserEntity have vehicle registered already.");

        /**
         * One vehicle can't be registered twice.
         */
        if(vehicleDao.existsVehicleByRegistrationNumber(reg_number)) throw new VehicleRegistrationForbiddenException("Vehicle with registration number " + reg_number + " already exists in database.");

        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNumber(reg_number);
        vehicle.setUser(userEntity);
        vehicleDao.save(vehicle);
        return vehicle;
    }



    /**
     * Delete userEntity's vehicle if userEntity's vehicle owner
     * @param reg_number
     * @throws VehicleRemovingForbiddenException
     */
    @DeleteMapping("/vehicle/delete/{reg_number}")
    private void deleteVehicle(Principal principal,
                               @PathVariable String reg_number) throws VehicleRemovingForbiddenException
    {
        UserEntity userEntity = users.findByUsername(principal.getName());

        if(!vehicleDao.existsVehicleByUserAndRegistrationNumber(userEntity, reg_number.toUpperCase())) throw new VehicleRemovingForbiddenException("You dont have permission or vehicle doesnt exists.");
        vehicleDao.deleteByUserAndRegistrationNumber(userEntity, reg_number.toUpperCase());
    }



    /**
     * Show userEntity's vehicles
     */
    @GetMapping("/vehicle/show")
    private List<Vehicle> showVehicles(Principal principal)
    {
        UserEntity userEntity = users.findByUsername(principal.getName());

        return vehicleDao.findAllByUser(userEntity);
    }


    /**
     * Start parking meter
     * @param reg_number vehicle's registration number path variable
     * @return
     */
    @PostMapping("/parking/{reg_number}/start")
    @ResponseStatus(HttpStatus.CREATED)
    private Transaction startParking(Principal principal,
                                     @PathVariable String reg_number)
    {

        UserEntity userEntity = users.findByUsername(principal.getName());

        Vehicle vehicle = vehicleDao.findByUserAndRegistrationNumber(userEntity, reg_number.toUpperCase()).orElseThrow( () -> new VehicleNotFoundException(reg_number.toUpperCase()));

        /**
         * Check if userEntity have transaction with given reg_number already started
         */
        if(transactionDao.existsByUserAndVehicleAndEndTimeIsNull(userEntity, vehicle)) throw new TransactionAlreadyExistsException(reg_number.toUpperCase());

        return transactionDao.save(Transaction.builder().user(userEntity).payed(false).vehicle(vehicle).build());
    }


    /**
     * Returns ongoing transaction
     * @param reg_number
     * @return
     */
    @GetMapping("/parking/{reg_number}")
    private Transaction checkTransactions(Principal principal,
                                          @PathVariable String reg_number)
    {
        UserEntity userEntity = users.findByUsername(principal.getName());

        return transactionDao.findByUserAndVehicleAndEndTimeIsNull(userEntity, vehicleDao.findByUserAndRegistrationNumber(userEntity, reg_number.toUpperCase()).orElseThrow( () -> new VehicleNotFoundException(reg_number.toUpperCase())))
                .orElseThrow(() -> new TransactionDoesntExistsException("Vehicle registration number: " + reg_number));
    }

    /**
     * Stoping userEntity's parking meter
     * @param reg_number
     * @return
     */
    @PutMapping("/parking/{reg_number}/stop")
    @ResponseStatus(HttpStatus.CREATED)
    private Transaction stopParking(Principal principal,
                                    @PathVariable String reg_number)
    {

        UserEntity userEntity = users.findByUsername(principal.getName());


        Vehicle vehicle = vehicleDao.findByUserAndRegistrationNumber(userEntity, reg_number.toUpperCase()).orElseThrow( () -> new VehicleNotFoundException(reg_number.toUpperCase()));

        Transaction transaction = transactionDao.findByUserAndVehicleAndEndTimeIsNull(userEntity, vehicle)
                .orElseThrow(() -> new TransactionDoesntExistsException("Vehicle registration number: " + reg_number.toUpperCase()));
        transaction.setEndTime(LocalDateTime.now());
        return transactionDao.save(transaction);
    }

    @GetMapping("/parking/history")
    public List<Transaction> showHistory(Principal principal)
    {
        UserEntity userEntity = users.findByUsername(principal.getName());

        return transactionDao.findAllByUserAndEndTimeNotNull(userEntity);
    }
}

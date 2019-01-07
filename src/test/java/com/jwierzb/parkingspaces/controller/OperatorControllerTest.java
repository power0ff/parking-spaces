package com.jwierzb.parkingspaces.controller;

import com.jwierzb.parkingspaces.MainApplication;
import com.jwierzb.parkingspaces.dao.*;
import com.jwierzb.parkingspaces.entity.Transaction;
import com.jwierzb.parkingspaces.entity.UserEntity;
import com.jwierzb.parkingspaces.entity.Vehicle;
import com.jwierzb.parkingspaces.security.JwtTokenProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {OperatorController.class, MainApplication.class})
@WebMvcTest
public class OperatorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserDao userDao;
    @MockBean
    DriverTypeDao driverTypeDao;
    @MockBean
    RoleDao roleDao;
    @MockBean
    CurrencyDao currencyDao;
    @MockBean
    VehicleDao vehicleDao;
    @MockBean
    TransactionDao transactionDao;
    @MockBean
    JwtTokenProvider jwtTokenProvider;


    @GetMapping("/check/{reg_number}")

    @Test
    @WithMockUser(roles = "NOT_OPERATOR_ROLE")
    public void shouldForbidAccesToEndpoint() throws Exception {
        mockMvc.perform(get("/operator/check/asdf")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    public void shouldSignalVehicleIsNotInDatabase() throws Exception {

        when(vehicleDao.findByRegistrationNumber(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/operator/check/asdf"))
                .andExpect(status().isOk())
                .andExpect(content().string("Vehicle is not in database"));

    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    public void shouldSignalsVehicleHasnotStartedParkometer() throws Exception {

        String regNumber = "redddg";

        UserEntity user = UserEntity.builder().username("user").build();
        Vehicle vehicle = Vehicle.builder().id(0L).registrationNumber(regNumber.toUpperCase()).user(user).build();
        Transaction transaction = Transaction.builder().endTime(null).user(user).startTime(LocalDateTime.now()).vehicle(vehicle).build();

        //vehicle is in db
        when(vehicleDao.findByRegistrationNumber(any())).thenReturn(Optional.of(vehicle));

        //vehicle has not ongoing transaction
        when(transactionDao.findByVehicleAndEndTimeIsNull(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/operator/check/" + regNumber))
                .andExpect(status().isOk())
                .andExpect(content().string("Vehicle doesn't started parking meter!"));

    }
    @Test
    @WithMockUser(roles = "OPERATOR")
    public void shouldSignalsVehicleIsWellParked() throws Exception {

        String regNumber = "redddg";

        UserEntity user = UserEntity.builder().username("user").build();
        Vehicle vehicle = Vehicle.builder().id(0L).registrationNumber(regNumber.toUpperCase()).user(user).build();
        Transaction transaction = Transaction.builder().endTime(null).user(user).startTime(LocalDateTime.now()).vehicle(vehicle).build();

        //vehicle is in db
        when(vehicleDao.findByRegistrationNumber(any())).thenReturn(Optional.of(vehicle));

        //vehicle has not ongoing transaction
        when(transactionDao.findByVehicleAndEndTimeIsNull(any())).thenReturn(Optional.of(transaction));

        mockMvc.perform(get("/operator/check/" + regNumber))
                .andExpect(status().isOk())
                .andExpect(content().string("Vehicle started parkometer"));

    }
}
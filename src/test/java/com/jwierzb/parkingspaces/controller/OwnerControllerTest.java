package com.jwierzb.parkingspaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwierzb.parkingspaces.MainApplication;
import com.jwierzb.parkingspaces.dao.*;
import com.jwierzb.parkingspaces.entity.Transaction;
import com.jwierzb.parkingspaces.entity.UserEntity;
import com.jwierzb.parkingspaces.entity.Vehicle;
import com.jwierzb.parkingspaces.model.OwnerProfitModel;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {OwnerController.class, MainApplication.class})
@WebMvcTest
public class OwnerControllerTest {

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


    @Test
    @WithMockUser(username = "asdf", roles = "NOT_OWNER_ROLE")
    public void shouldForbidAccesToEndopintDueToLackOfRoles() throws Exception {
        mockMvc.perform(get("/owner/anything/profit")).andExpect(status().isForbidden());
    }
    @Test
    @WithMockUser(username = "asdf", roles = "OWNER")
    public void shouldShowZeroProfit() throws Exception {

        String date = "2019-01-07";

        mockMvc.perform(get("/owner/"+ date +"/profit")).andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "asdf", roles = "OWNER")
    public void shouldShowProfit() throws Exception {

        String date = "2019-01-07";
        LocalDateTime start1 = LocalDateTime.parse("2019-01-07T11:36:50");
        LocalDateTime start2 = LocalDateTime.parse("2019-01-07T09:36:50");
        LocalDateTime start3 = LocalDateTime.parse("2019-01-07T15:36:50");

        LocalDateTime end1 = start1.plusHours(2L);
        LocalDateTime end2 = start1.plusHours(9L).plusMinutes(20L);
        LocalDateTime end3 = start1.plusHours(5L);


        OwnerProfitModel ownerProfitModel = OwnerProfitModel.builder().totalProfit(45D).parkingFinished(3).parkingInProgress(0).build();

        UserEntity user = UserEntity.builder().username("asdf").build();
        Vehicle vehicle = Vehicle.builder().registrationNumber("asdf").user(user).build();

        Transaction transaction1 = Transaction.builder().endTime(end1).user(user).startTime(start1).vehicle(vehicle).price(10D).build();
        Transaction transaction3 = Transaction.builder().endTime(end2).user(user).startTime(start2).vehicle(vehicle).price(20D).build();
        Transaction transaction4 = Transaction.builder().endTime(end3).user(user).startTime(start3).vehicle(vehicle).price(15D).build();

        List<Transaction> list = new ArrayList<>();
        list.add(transaction1);
        list.add(transaction3);
        list.add(transaction4);

        when(transactionDao.countAllByEndTimeBetween(any(), any())).thenReturn(3);
        when(transactionDao.countAllByEndTimeNull()).thenReturn(0);
        when(transactionDao.findAllByEndTimeBetween(any(),any())).thenReturn(list);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ownerProfitModel);

        mockMvc.perform(get("/owner/"+ date+"/profit"))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonInString));
    }
}
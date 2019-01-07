package com.jwierzb.parkingspaces.dao;

import com.jwierzb.parkingspaces.MainApplication;
import com.jwierzb.parkingspaces.controller.DriverController;
import com.jwierzb.parkingspaces.entity.Transaction;
import com.jwierzb.parkingspaces.entity.UserEntity;
import com.jwierzb.parkingspaces.entity.Vehicle;
import com.jwierzb.parkingspaces.model.OwnerProfitModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TransactionDaoTest {

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



    UserEntity userEntity;

    @Before
    public void init() throws Exception {
        userEntity = UserEntity.builder()
                .username("transaction.dao")
                .password("test")
                .enabled(true)
                .phoneNumber(737737737)
                .driverType(driverTypeDao.findByName("disabled").orElseThrow(() -> new Exception("Driver Type incompatible.")))
                .email("transaction.dao@pl")
                .firstName("test")
                .lastName("test")
                .currency(currencyDao.findByName("PLN").orElseThrow(() -> new Exception("Currency username incompatible.")))
                .nonLocked(true)
                .role(roleDao.findAllByName("DRIVER"))
                .build();
    }

    @Test
    public void countAllByEndTimeBetweenShouldGive3()
    {
        UserEntity user = users.save(userEntity);
        Vehicle vehicle = vehicleDao.save(Vehicle.builder().registrationNumber("asdf").user(user).build());
        String date = "2019-01-07";

        LocalDateTime start = LocalDateTime.parse("2019-01-07T11:36:50");

        LocalDateTime start1 = LocalDateTime.parse("2019-01-07T11:36:50");
        LocalDateTime start2 = LocalDateTime.parse("2019-01-07T09:36:50");
        LocalDateTime start3 = LocalDateTime.parse("2019-01-07T15:36:50");

        LocalDateTime end1 = start1.plusHours(2L);
        LocalDateTime end2 = start1.plusHours(9L).plusMinutes(20L);
        LocalDateTime end3 = start1.plusHours(5L);

        Transaction transaction1 = Transaction.builder().endTime(end1).user(user).startTime(start1).vehicle(vehicle).build();
        Transaction transaction3 = Transaction.builder().endTime(end2).user(user).startTime(start2).vehicle(vehicle).build();
        Transaction transaction4 = Transaction.builder().endTime(end3).user(user).startTime(start3).vehicle(vehicle).build();
        List<Transaction> list = new ArrayList<>();
        list.add(transaction1);
        list.add(transaction3);
        list.add(transaction4);
        List<Transaction> transactionList = transactionDao.saveAll(list);

        Assert.assertEquals(3L, (long)transactionDao.countAllByEndTimeBetween(start, start.plusDays(1L)));

        transactionDao.deleteAll(transactionList);
        vehicleDao.delete(vehicle);
        users.delete(user);

    }
    @Test
    public void countAllByEndTimeBetweenShouldGive0()
    {
        UserEntity user = users.save(userEntity);
        Vehicle vehicle = vehicleDao.save(Vehicle.builder().registrationNumber("asdf").user(user).build());

        String date = "2019-01-08";

        LocalDateTime start = LocalDateTime.parse("2019-01-07T11:36:50");

        LocalDateTime start1 = LocalDateTime.parse("2019-01-07T11:36:50");
        LocalDateTime start2 = LocalDateTime.parse("2019-01-07T09:36:50");
        LocalDateTime start3 = LocalDateTime.parse("2019-01-07T15:36:50");

        LocalDateTime end1 = start1.plusHours(2L);
        LocalDateTime end2 = start1.plusHours(9L).plusMinutes(20L); //10 payed hours
        LocalDateTime end3 = start1.plusHours(5L);

        Transaction transaction1 = Transaction.builder().endTime(end1).user(user).startTime(start1).vehicle(vehicle).build();
        Transaction transaction3 = Transaction.builder().endTime(end2).user(user).startTime(start2).vehicle(vehicle).build();
        Transaction transaction4 = Transaction.builder().endTime(end3).user(user).startTime(start3).vehicle(vehicle).build();
        List<Transaction> list = new ArrayList<>();
        list.add(transaction1);
        list.add(transaction3);
        list.add(transaction4);
        List<Transaction> transactionList = transactionDao.saveAll(list);

        Assert.assertEquals(3L, (long)transactionDao.countAllByEndTimeBetween(start, start.plusDays(1L)));
        transactionDao.deleteAll(transactionList);
        vehicleDao.delete(vehicle);
        users.delete(user);

    }

    @Test
    public void countAllByEndTimeBetweenShouldGive1()
    {
        UserEntity user = users.save(userEntity);
        Vehicle vehicle = vehicleDao.save(Vehicle.builder().registrationNumber("asdf").user(user).build());


        String date = "2019-01-07";

        LocalDateTime start = LocalDateTime.parse("2019-01-07T11:36:50");

        LocalDateTime start1 = LocalDateTime.parse("2019-01-07T11:36:50");
        ///dayy later
        LocalDateTime start2 = LocalDateTime.parse("2019-01-08T09:36:50");
        LocalDateTime start3 = LocalDateTime.parse("2019-01-08T15:36:50");

        LocalDateTime end1 = start1.plusHours(2L);
        LocalDateTime end2 = start1.plusHours(9L).plusMinutes(20L);
        LocalDateTime end3 = start1.plusHours(5L);

        Transaction transaction1 = Transaction.builder().endTime(end1).user(user).startTime(start1).vehicle(vehicle).build();
        Transaction transaction3 = Transaction.builder().endTime(end2).user(user).startTime(start2).vehicle(vehicle).build();
        Transaction transaction4 = Transaction.builder().endTime(end3).user(user).startTime(start3).vehicle(vehicle).build();
        List<Transaction> list = new ArrayList<>();
        list.add(transaction1);
        list.add(transaction3);
        list.add(transaction4);
        List<Transaction> transactionList = transactionDao.saveAll(list);

        Assert.assertEquals(3L, (long)transactionDao.countAllByEndTimeBetween(start, start.plusDays(1L)));


        transactionDao.deleteAll(transactionList);
        vehicleDao.delete(vehicle);
        users.delete(user);

    }

}
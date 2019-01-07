package com.jwierzb.parkingspaces.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwierzb.parkingspaces.MainApplication;
import com.jwierzb.parkingspaces.dao.*;
import com.jwierzb.parkingspaces.entity.Transaction;
import com.jwierzb.parkingspaces.entity.UserEntity;
import com.jwierzb.parkingspaces.entity.Vehicle;
import com.jwierzb.parkingspaces.exception.TransactionDoesntExistsException;
import com.jwierzb.parkingspaces.exception.VehicleNotFoundException;
import com.jwierzb.parkingspaces.security.JwtTokenProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DriverController.class, MainApplication.class})
@WebMvcTest
public class DriverControllerTest {

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
    @WithMockUser(username = "asdf", roles = "NOT_DRIVER_ROLE")
    public void shouldForbidAccesToEndopintDueToLackOfRoles() throws Exception {
        mockMvc.perform(get("/driver/vehicle/show")).andExpect(status().isForbidden());
        mockMvc.perform(post("/driver/vehicle/register/test")).andExpect(status().isForbidden());
        mockMvc.perform(delete("/driver/vehicle/delete/test")).andExpect(status().isForbidden());
        mockMvc.perform(post("/driver/parking/test/start")).andExpect(status().isForbidden());
        mockMvc.perform(put("/driver/parking/test/stop")).andExpect(status().isForbidden());
        mockMvc.perform(get("/driver/parking/test")).andExpect(status().isForbidden());
        mockMvc.perform(get("/driver/parking/history")).andExpect(status().isForbidden());

    }


    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void shouldAddVehicleToDriver() throws Exception {

        String regNumber = "redddg";
        UserEntity user = UserEntity.builder().username("user").build();
        Vehicle vehicle = Vehicle.builder().id(0L).registrationNumber(regNumber.toUpperCase()).user(user).build();

        when(vehicleDao.existsVehicleByUser(anyObject())).thenReturn(false);
        when(vehicleDao.existsVehicleByRegistrationNumber(anyString())).thenReturn(false);
        when(vehicleDao.save(anyObject())).thenReturn(vehicle);
        when(userDao.findByUsername(anyString())).thenReturn(user);

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vehicle);

        mockMvc.perform(post("/driver/vehicle/register/"+regNumber))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonInString));

    }
    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void shouldntAddVehicleToDriverHavingVehicle() throws Exception {

        String regNumber = "redddg";

        when(vehicleDao.existsVehicleByUser(anyObject())).thenReturn(true);

        mockMvc.perform(post("/driver/vehicle/register/"+regNumber))
                .andExpect(status().isForbidden());

    }
    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void shouldntAddVehicleTwice() throws Exception {

        String regNumber = "redddg";

        //vehicle exists in db
        when(vehicleDao.existsVehicleByRegistrationNumber(anyString())).thenReturn(true);

        mockMvc.perform(post("/driver/vehicle/register/"+regNumber))
                .andExpect(status().isForbidden());

    }
    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void shouldRemoveVehicle() throws Exception {

        String regNumber = "redddg";

        when(vehicleDao.existsVehicleByUserAndRegistrationNumber(any(), any())).thenReturn(true);

        mockMvc.perform(delete("/driver/vehicle/delete/"+regNumber)).andExpect(status().isOk());

    }
    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void shouldntRemoveVehicle_VehicleDoesntExists() throws Exception {

        String regNumber = "redddg";

        when(vehicleDao.existsVehicleByUserAndRegistrationNumber(any(), any())).thenReturn(false);

        mockMvc.perform(delete("/driver/vehicle/delete/"+regNumber)).andExpect(status().isForbidden()).andExpect(content().string("Removing failed. You dont have permission or vehicle doesnt exists."));
    }

    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void shouldShowOneVehicle() throws Exception {

        Vehicle vehicle = Vehicle.builder().id(0L).registrationNumber("reg").user(UserEntity.builder().username("user").build()).build();
        List<Vehicle> list = new ArrayList<>();
        list.add(vehicle);
        when(vehicleDao.findAllByUser(any())).thenReturn(list);

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);

        mockMvc.perform(get("/driver/vehicle/show"))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonInString));
    }

    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void shouldShowZeroVehicles() throws Exception {


        when(vehicleDao.findAllByUser(any())).thenReturn(Collections.emptyList());

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(Collections.emptyList());

        mockMvc.perform(get("/driver/vehicle/show"))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonInString));
    }

    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void shouldBeParked() throws Exception {

        UserEntity user = UserEntity.builder().username("asdf").build();
        Vehicle vehicle = Vehicle.builder().registrationNumber("asdf").user(user).build();
        Transaction transaction = Transaction.builder().endTime(null).user(user).startTime(LocalDateTime.now()).vehicle(vehicle).build();

        when(vehicleDao.findByUserAndRegistrationNumber(any(), any())).thenReturn(Optional.of(vehicle));
        when(userDao.findByUsername(any())).thenReturn(user);

        when(transactionDao.findByUserAndVehicleAndEndTimeIsNull(any(), any())).thenReturn(Optional.of(transaction));

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(transaction);

        mockMvc.perform(get("/driver/parking/show"))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonInString));

    }
    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void shouldNotBeParked_VehicleDoesntExists() throws Exception {

        UserEntity user = UserEntity.builder().username("asdf").build();
        Vehicle vehicle = Vehicle.builder().registrationNumber("asdf").user(user).build();
        Transaction transaction = Transaction.builder().endTime(null).user(user).startTime(LocalDateTime.now()).vehicle(vehicle).build();

        when(vehicleDao.findByUserAndRegistrationNumber(any(), any())).thenThrow(new VehicleNotFoundException("asdf".toUpperCase()));

        String reg = "asdf";
        mockMvc.perform(get("/driver/parking/asdf"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Vehicle "+reg.toUpperCase()+" not found or you are not the owner."));


    }
    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void shouldNotBeParked_TransactionOpenednt() throws Exception {

        UserEntity user = UserEntity.builder().username("asdf").build();
        Vehicle vehicle = Vehicle.builder().registrationNumber("asdf").user(user).build();
        Transaction transaction = Transaction.builder().endTime(null).user(user).startTime(LocalDateTime.now()).vehicle(vehicle).build();


        String reg = "asdf";

        when(vehicleDao.findByUserAndRegistrationNumber(any(), any())).thenReturn(Optional.of(new Vehicle()));
        when(transactionDao.findByUserAndVehicleAndEndTimeIsNull(any(), any())).thenThrow(new TransactionDoesntExistsException("Vehicle registration number: " + reg.toUpperCase()));
        mockMvc.perform(get("/driver/parking/"+reg))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transaction doesnt exists. " + "Vehicle registration number: " + reg.toUpperCase()));

    }


    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void parkingShouldBeStarted_ifUserHaveVehicleNotParked() throws Exception {

        UserEntity user = UserEntity.builder().username("asdf").build();
        Vehicle vehicle = Vehicle.builder().registrationNumber("asdf").user(user).build();
        Transaction transaction = Transaction.builder().endTime(null).user(user).startTime(LocalDateTime.now()).vehicle(vehicle).build();


        String reg = "asdf";

        when(vehicleDao.findByUserAndRegistrationNumber(any(), any())).thenReturn(Optional.of(new Vehicle()));

        //null end time means there is no ongoing transactoins with thta vehicle
        when(transactionDao.existsByUserAndVehicleAndEndTimeIsNull(any(), any())).thenReturn(false);
        when(transactionDao.save(any())).thenReturn(transaction);

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(transaction);

        mockMvc.perform(post("/driver/parking/"+reg+"/start"))
                .andExpect(status().isCreated())
                .andExpect(content().string(jsonInString));

    }
    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void parkingShoulNotdBeStarted_ifUserHaveVehicleParked() throws Exception {

        Vehicle vehicle = Vehicle.builder().registrationNumber("asdf").user(new UserEntity()).build();

        String reg = "asdf";

        when(vehicleDao.findByUserAndRegistrationNumber(any(), any())).thenReturn(Optional.of(new Vehicle()));

        //uesr have vehicle parked
        when(transactionDao.existsByUserAndVehicleAndEndTimeIsNull(any(), any())).thenReturn(true);


        mockMvc.perform(post("/driver/parking/"+reg+"/start"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Vehicle is already parked. Vehicle reg number: " + reg.toUpperCase()));

    }

    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void parkingShoulBeStopped_ifUserHaveVehicleParked() throws Exception {

        UserEntity user = UserEntity.builder().username("asdf").build();
        Vehicle vehicle = Vehicle.builder().registrationNumber("asdf").user(user).build();
        Transaction transaction = Transaction.builder().endTime(null).user(user).startTime(LocalDateTime.now()).vehicle(vehicle).build();

        String reg = "asdf";

        when(vehicleDao.findByUserAndRegistrationNumber(any(), any())).thenReturn(Optional.of(new Vehicle()));

        //user have vehicle parked
        when(transactionDao.findByUserAndVehicleAndEndTimeIsNull(any(), any())).thenReturn(Optional.of(transaction));
        when(transactionDao.save(any())).thenReturn(transaction);


        mockMvc.perform(put("/driver/parking/" + reg + "/stop"))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void parkingShouldntBeStopped_ifUserHaventVehicle() throws Exception {

        UserEntity user = UserEntity.builder().username("asdf").build();
        Vehicle vehicle = Vehicle.builder().registrationNumber("asdf").user(user).build();
        Transaction transaction = Transaction.builder().endTime(null).user(user).startTime(LocalDateTime.now()).vehicle(vehicle).build();

        String reg = "asdf";

        //user doesnt have vehicle
        when(vehicleDao.findByUserAndRegistrationNumber(any(), any())).thenReturn(Optional.empty());


        mockMvc.perform(put("/driver/parking/" + reg + "/stop"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Vehicle " + reg.toUpperCase() + " not found or you are not the owner."));
    }

    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void parkingShouldntBeStopped_ifUserHaventVehicleParked() throws Exception {

        UserEntity user = UserEntity.builder().username("asdf").build();
        Vehicle vehicle = Vehicle.builder().registrationNumber("asdf").user(user).build();
        Transaction transaction = Transaction.builder().endTime(null).user(user).startTime(LocalDateTime.now()).vehicle(vehicle).build();

        String reg = "asdf";

        //user doesnt have vehicle
        when(vehicleDao.findByUserAndRegistrationNumber(any(), any())).thenReturn(Optional.of(new Vehicle()));

        //but not parked
        when(transactionDao.findByUserAndVehicleAndEndTimeIsNull(any(), any())).thenReturn(Optional.empty());


        mockMvc.perform(put("/driver/parking/" + reg + "/stop"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transaction doesnt exists. Vehicle registration number: "+ reg.toUpperCase()));
    }

    @Test
    @WithMockUser(username = "test_user", roles = {"DRIVER"})
    public void historyShouldntBeNull() throws Exception {

        UserEntity user = UserEntity.builder().username("asdf").build();
        Vehicle vehicle = Vehicle.builder().registrationNumber("asdf").user(user).build();
        Transaction transaction1 = Transaction.builder().endTime(LocalDateTime.now()).user(user).startTime(LocalDateTime.now()).vehicle(vehicle).build();
        Transaction transaction2 = Transaction.builder().endTime(LocalDateTime.now()).user(user).startTime(LocalDateTime.now()).vehicle(vehicle).build();

        List<Transaction> list = new ArrayList<>();
        list.add(transaction1);
        list.add(transaction2);

        //but not parked
        when(transactionDao.findAllByUserAndEndTimeNotNull(any())).thenReturn(list);

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);

        mockMvc.perform(get("/driver/parking/history"))
                .andExpect(status().isOk())
                .andExpect(content().string(jsonInString));
    }

}

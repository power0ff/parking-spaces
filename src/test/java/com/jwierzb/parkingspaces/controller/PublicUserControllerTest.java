package com.jwierzb.parkingspaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwierzb.parkingspaces.MainApplication;
import com.jwierzb.parkingspaces.dao.TransactionDao;
import com.jwierzb.parkingspaces.dao.UserDao;
import com.jwierzb.parkingspaces.model.UserModel;
import com.jwierzb.parkingspaces.model.UserModelLogin;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PublicUserControllerTest {

    @Autowired
    UserDao userDao;
    @Autowired
    MockMvc mockMvc;

    UserModel userModel =
            UserModel.builder()
                    .currency("PLN").driverType("regular")
                    .email("test.public@pl").firstName("test")
                    .lastName("test").password("pass")
                    .phoneNumber(737).username("test.public")
                    .build();

    UserModelLogin userModelLogin = UserModelLogin.builder().password("pass").username("test.public").build();


    @Test
    public void shouldRegisterUserAndThenGiveToken() throws Exception {


        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userModel);

        //user doesnt exists in db
        Assert.assertEquals(false, userDao.existsByUsername(userModel.getUsername()));

        //give Created statusexit
        mockMvc.perform(post("/user/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInString)).andExpect(status().isCreated());

        //user exists in db
        Assert.assertEquals(true, userDao.existsByUsername(userModel.getUsername()));


        jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userModelLogin);


        //given token
        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

    }
}
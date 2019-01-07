package com.jwierzb.parkingspaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwierzb.parkingspaces.MainApplication;
import com.jwierzb.parkingspaces.dao.UserDao;
import com.jwierzb.parkingspaces.entity.UserEntity;
import com.jwierzb.parkingspaces.model.UserModel;
import com.jwierzb.parkingspaces.model.UserModelLogin;
import com.jwierzb.parkingspaces.security.TokenWraper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SecuredUserControllerTest {

    @Autowired
    UserDao userDao;
    @Autowired
    MockMvc mockMvc;

    UserModel userModel =
            UserModel.builder()
                    .currency("PLN").driverType("regular")
                    .email("test.secured@pl").firstName("test")
                    .lastName("test").password("pass")
                    .phoneNumber(737).username("test.secured")
                    .build();

    UserModelLogin userModelLogin = UserModelLogin.builder().password("pass").username("test").build();

    String token;

    @Before
    public void init() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userModel);

        //give Created status
        mockMvc.perform(post("/user/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInString)).andExpect(status().isCreated());

        //given token
        MvcResult result = mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists()).andReturn();

        TokenWraper token_tmp = mapper.reader().forType(TokenWraper.class).readValue(result.getResponse().getContentAsString());
        token = token_tmp.getToken();
    }
    @Test
    public void shouldReturnUsersBody() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        UserEntity user = userDao.findByUsername(userModel.getUsername());

        mockMvc.perform(get("/user/current")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user)));
    }
}
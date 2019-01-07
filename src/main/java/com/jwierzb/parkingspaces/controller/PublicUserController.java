package com.jwierzb.parkingspaces.controller;


import com.jwierzb.parkingspaces.dao.CurrencyDao;
import com.jwierzb.parkingspaces.dao.DriverTypeDao;
import com.jwierzb.parkingspaces.dao.RoleDao;
import com.jwierzb.parkingspaces.dao.UserDao;
import com.jwierzb.parkingspaces.entity.UserEntity;
import com.jwierzb.parkingspaces.exception.CustomException;
import com.jwierzb.parkingspaces.model.UserModelLogin;
import com.jwierzb.parkingspaces.security.JwtTokenProvider;
import com.jwierzb.parkingspaces.security.TokenWraper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import com.jwierzb.parkingspaces.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Controller responsible for login and signing up users.
 */
@RestController
@RequestMapping("/user")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicUserController {


    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserDao users;
    @Autowired
    DriverTypeDao driverTypeDao;
    @Autowired
    CurrencyDao currencyDao;
    @Autowired
    RoleDao roleDao;



    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    private void register(@RequestBody UserModel userModel) throws Exception {
        if(users.existsByUsername(userModel.getUsername()) || users.existsByEmail(userModel.getEmail())) throw new Exception(); //TODO better exception
        UserEntity userEntity =
                UserEntity.builder()
                .username(userModel.getUsername())
                .password((userModel.getPassword()))
                .enabled(true)
                .phoneNumber(userModel.getPhoneNumber())
                .driverType(driverTypeDao.findByName(userModel.getDriverType()).orElseThrow(() ->  new Exception("Driver Type incompatible."+userModel.getDriverType())))
                .email(userModel.getEmail())
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .currency(currencyDao.findByName(userModel.getCurrency()).orElseThrow(() ->  new Exception("Currency username incompatible."+userModel.getCurrency())))
                .nonLocked(true)
                .role(roleDao.findAllByName("DRIVER"))
                .build();
        users.save(userEntity);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    private TokenWraper login(@RequestBody UserModelLogin userModel)  {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userModel.getUsername(), userModel.getPassword()));
            return new TokenWraper(jwtTokenProvider.createToken(userModel.getUsername(), users.findByUsername(userModel.getUsername()).getRole()));
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }



}



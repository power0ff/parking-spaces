package com.jwierzb.parkingspaces.controller;

import com.jwierzb.parkingspaces.dao.UserDao;
import com.jwierzb.parkingspaces.entity.UserEntity;
import com.jwierzb.parkingspaces.security.TokenWraper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/user")
public class SecuredUserController {

    @Autowired
    UserDao userDao;

    /**
     * Shows info about userEntity with specified token given in json header.
     * TODO return hateoas resources
     * @return
     */
    @GetMapping(path = "/current")
    @ResponseBody
    public UserEntity getCurrent(Principal principal) {

        return userDao.findByUsername(principal.getName());
    }




}
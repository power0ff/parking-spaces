package com.jwierzb.parkingspaces.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class UserModel {

    @NotNull
    String driverType;

    @NotNull
    @JsonIgnore
    String currency = "PLN";

    @NotNull
    Integer phoneNumber;

    @NotNull
    String firstName;

    @NotNull
    String lastName;

    @NotNull
    @Email
    String email;

    @NotNull
    String password;

    @NotNull
    String username;

    UserModel(){}
}

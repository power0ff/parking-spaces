package com.jwierzb.parkingspaces.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserModelLogin {
    String username;
    String password;

    UserModelLogin(){}
}

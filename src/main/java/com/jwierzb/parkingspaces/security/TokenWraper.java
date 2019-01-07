package com.jwierzb.parkingspaces.security;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class TokenWraper {

    @NonNull
    private String token;
    TokenWraper(){}
}

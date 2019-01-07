package com.jwierzb.parkingspaces.exception;


public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String id) {
        super("Could not find userEntity " + id);
    }
}
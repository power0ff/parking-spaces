package com.jwierzb.parkingspaces.exception;


public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String username) {
        super("Invalid password or username.");
    }
}
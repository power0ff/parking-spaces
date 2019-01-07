package com.jwierzb.parkingspaces.exception;


public class VehicleRegistrationForbiddenException extends RuntimeException {


    public VehicleRegistrationForbiddenException(String er) {
        super("Vehicle registration forbidden. " + er);
    }
}
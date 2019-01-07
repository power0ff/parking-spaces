package com.jwierzb.parkingspaces.exception;

public class VehicleRemovingForbiddenException extends RuntimeException {
    public VehicleRemovingForbiddenException(String er) {super("Removing failed. "+ er );}
}
package com.jwierzb.parkingspaces.exception;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(String msg) {super("Vehicle " + msg + " not found or you are not the owner.");}

}

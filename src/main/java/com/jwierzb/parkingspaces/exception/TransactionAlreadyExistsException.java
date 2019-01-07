package com.jwierzb.parkingspaces.exception;

public class TransactionAlreadyExistsException extends RuntimeException {
    public TransactionAlreadyExistsException(String msg) {super("Vehicle is already parked. Vehicle reg number: " + msg );}

}

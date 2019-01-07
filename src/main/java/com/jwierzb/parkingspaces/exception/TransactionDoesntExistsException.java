package com.jwierzb.parkingspaces.exception;

public class TransactionDoesntExistsException extends RuntimeException {
    public TransactionDoesntExistsException(String msg) {super("Transaction doesnt exists. " + msg );}

}

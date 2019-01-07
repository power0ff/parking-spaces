package com.jwierzb.parkingspaces.exception;


import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class SecuredUserControllerAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(VehicleRegistrationForbiddenException.class)
    public String vehicleRegistrationForbidden(VehicleRegistrationForbiddenException ex) {
        return new String ( ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(VehicleRemovingForbiddenException.class)
    public String vehicleRemovingForbidden(VehicleRemovingForbiddenException ex) {
        return new String ( ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(VehicleNotFoundException.class)
    public String vehicleNotFoundException(VehicleNotFoundException ex) {
        return new String ( ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(TransactionAlreadyExistsException.class)
    public String transactionAlreadyExistsException(TransactionAlreadyExistsException ex) {
        return new String ( ex.getMessage());
    }
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TransactionDoesntExistsException.class)
    public String transactionDoesntExistsException(TransactionDoesntExistsException ex) {
        return new String ( ex.getMessage());
    }
}


package com.donation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;
@ControllerAdvice
public class CustomExceptionHandler {
   @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<Object> handleBadRequestException(BadRequestException e){

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ExceptionRequest exceptionRequest = new ExceptionRequest(
                e.getMessage(),
                badRequest,
                ZonedDateTime.now()
        );
   return new ResponseEntity<>(exceptionRequest, badRequest);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> notFoundException(NotFoundException e){

        HttpStatus badRequest = HttpStatus.NOT_FOUND;
        ExceptionRequest exceptionRequest = new ExceptionRequest(
                e.getMessage(),
                badRequest,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(exceptionRequest, badRequest);
    }

    @ExceptionHandler(value = {InsufficientAvailableAmountException.class})
    public  ResponseEntity<Object> InsufficientAvailableAmountExceptionHandle(InsufficientAvailableAmountException e){
        HttpStatus badRequest = HttpStatus.NOT_FOUND;
        ExceptionRequest exceptionRequest = new ExceptionRequest(
                e.getMessage(),
                badRequest,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(exceptionRequest, badRequest);
    }
}

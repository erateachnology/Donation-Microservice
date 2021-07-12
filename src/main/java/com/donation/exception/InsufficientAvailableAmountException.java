package com.donation.exception;

public class InsufficientAvailableAmountException extends RuntimeException{

    public InsufficientAvailableAmountException(String message){
        super(message);
    }

    public InsufficientAvailableAmountException(String message, Throwable cause){
        super(message,cause);
    }
}

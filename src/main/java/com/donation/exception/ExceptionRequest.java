package com.donation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
public class ExceptionRequest {

    private String message;
    private HttpStatus httpStatus;
    private ZonedDateTime timeStamp;
}

package com.donation.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
public class Exception {

    private String message;
    private HttpStatus httpStatus;
    private ZonedDateTime timeStamp;
}

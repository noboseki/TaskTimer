package com.noboseki.tasktimer.exeption;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
@ToString
@RequiredArgsConstructor
public class ApiException {

    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timestamp;
}

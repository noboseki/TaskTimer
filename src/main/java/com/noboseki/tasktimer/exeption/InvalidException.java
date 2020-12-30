package com.noboseki.tasktimer.exeption;

import lombok.Getter;

@Getter
public class InvalidException extends RuntimeException {
    private String fieldName;
    private String filedValue;

    public InvalidException(String fieldName, String filedValue) {
        super(String.format("Invalid value of '%s' : '%s' ", fieldName, filedValue));
        this.fieldName = fieldName;
        this.filedValue = filedValue;
    }
}

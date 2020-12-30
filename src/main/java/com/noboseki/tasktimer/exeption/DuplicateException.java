package com.noboseki.tasktimer.exeption;

import lombok.Getter;

@Getter
public class DuplicateException extends RuntimeException {
    private String objectName;
    private String fieldName;
    private String filedValue;

    public DuplicateException(String objectName, String fieldName, String filedValue) {
        super(String.format("%s with '%s' : '%s' exists in the database", objectName, fieldName, filedValue));
        this.objectName = objectName;
        this.fieldName = fieldName;
        this.filedValue = filedValue;
    }
}

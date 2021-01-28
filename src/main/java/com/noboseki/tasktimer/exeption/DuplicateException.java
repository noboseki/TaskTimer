package com.noboseki.tasktimer.exeption;

import lombok.Getter;

@Getter
public class DuplicateException extends RuntimeException {
    private String objectName;
    private String filedValue;

    public DuplicateException(String objectName, String filedValue) {
        super(ExceptionTextConstants.duplicate(objectName, filedValue));
        this.objectName = objectName;
        this.filedValue = filedValue;
    }
}

package com.noboseki.tasktimer.exeption;

import lombok.Getter;

@Getter
public class SaveException extends RuntimeException {
    private String fieldName;
    private String fieldValue;

    public SaveException(String fieldName, String fieldValue) {
        super(ExceptionTextConstants.save(fieldName, fieldValue));
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}

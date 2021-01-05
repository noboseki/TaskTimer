package com.noboseki.tasktimer.exeption;

import lombok.Getter;

@Getter
public class DeleteException extends RuntimeException {
    private String fieldName;
    private String fieldValue;


    public DeleteException(String fieldName, String fieldValue) {
        super(String.format("Delete error of '%s' : '%s'", fieldName, fieldValue));
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

}

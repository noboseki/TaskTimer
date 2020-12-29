package com.noboseki.tasktimer.exeption;

import lombok.Getter;

@Getter
public class DeleteException extends RuntimeException {
    private String objectName;
    private String fieldName;
    private String fieldValue;


    public DeleteException(String objectName, String fieldName, String fieldValue) {
        super(String.format("%s delete error of '%s' : '%s'", objectName, fieldName, fieldValue));
        this.objectName = objectName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

}

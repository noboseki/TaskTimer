package com.noboseki.tasktimer.exeption;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private String objectName;
    private String fieldName;
    private String fieldValue;

    public ResourceNotFoundException(String objectName, String fieldName, String fieldValue) {
        super(String.format("%s not found by %s : '%s'", objectName, fieldName, fieldValue));
        this.objectName = objectName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
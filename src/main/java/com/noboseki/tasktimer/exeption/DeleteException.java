package com.noboseki.tasktimer.exeption;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class DeleteException extends RuntimeException {
    private Object fieldValue;
    private String fieldName;

    public DeleteException(String fieldName, Object fieldValue) {
        super(String.format("%s delete error of id : '%s'", fieldName, fieldValue));
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}

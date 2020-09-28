package com.noboseki.tasktimer.exeption;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class SaveException extends RuntimeException {
    private Object fieldValue;
    private String fieldName;

    public SaveException(String fieldName, Object fieldValue) {
        super(String.format("%s save error of : '%s'", fieldName, fieldValue));
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}

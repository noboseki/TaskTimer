package com.noboseki.tasktimer.exeption;

import lombok.Getter;

@Getter
public class SaveException extends RuntimeException {
    private String fieldName;

    public SaveException(String fieldName) {
        super(String.format("Save error of : '%s'", fieldName));
        this.fieldName = fieldName;
    }
}

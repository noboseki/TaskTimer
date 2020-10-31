package com.noboseki.tasktimer.exeption;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DateTimeException extends RuntimeException {
    private String typeName;
    private String injectedString;

    public DateTimeException(String typeName, String injectedString) {
        super(String.format("Create error '%s' form string: '%s'", typeName, injectedString));
        this.typeName = typeName;
        this.injectedString = injectedString;
    }
}

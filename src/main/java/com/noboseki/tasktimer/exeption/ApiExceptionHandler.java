package com.noboseki.tasktimer.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {SaveException.class})
    public ResponseEntity<ApiException> handleSaveException(SaveException exception) {
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiException apiException = new ApiException(
                exception.getMessage(),
                internalServerError,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(apiException, internalServerError);
    }

    @ExceptionHandler(value = {DeleteException.class})
    public ResponseEntity<ApiException> handleDeleteException(DeleteException exception) {
        HttpStatus internalServerError = HttpStatus.METHOD_NOT_ALLOWED;

        ApiException apiException = new ApiException(
                exception.getMessage(),
                internalServerError,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(apiException, internalServerError);
    }

    @ExceptionHandler(value = {DuplicateException.class})
    public ResponseEntity<ApiException> handleDuplicateException(DuplicateException exception) {
        HttpStatus internalServerError = HttpStatus.CONFLICT;

        ApiException apiException = new ApiException(
                exception.getMessage(),
                internalServerError,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(apiException, internalServerError);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<ApiException> handleResourceNotFoundException(ResourceNotFoundException exception) {
        HttpStatus internalServerError = HttpStatus.NOT_FOUND;

        ApiException apiException = new ApiException(
                exception.getMessage(),
                internalServerError,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(apiException, internalServerError);
    }

    @ExceptionHandler(value = {DateTimeException.class})
    public ResponseEntity<ApiException> handleDateTimeException(DateTimeException exception) {
        HttpStatus internalServerError = HttpStatus.BAD_REQUEST;

        ApiException apiException = new ApiException(
                exception.getMessage(),
                internalServerError,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(apiException, internalServerError);
    }
}

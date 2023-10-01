package com.mycopmany.myproject.machineapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException e){
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        ApiException apiException = new ApiException(
                e.getMessage(),
                notFound,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, notFound);
    }
    @ExceptionHandler(value = {UnprocessableEntityException.class})
    public ResponseEntity<Object> handleEntityNotValid(UnprocessableEntityException e) {
        HttpStatus unprocessableEntity = HttpStatus.UNPROCESSABLE_ENTITY;
        ApiException apiException = new ApiException(
                e.getMessage(),
                unprocessableEntity,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(apiException, unprocessableEntity);
    }

        @ExceptionHandler(value = {ConflictException.class})
        public ResponseEntity<Object> handleConflictException(ConflictException e){
            HttpStatus conflict = HttpStatus.CONFLICT;
            ApiException apiException = new ApiException(
                    e.getMessage(),
                    conflict,
                    ZonedDateTime.now(ZoneId.of("Z"))
            );
            return new ResponseEntity<>(apiException, conflict);
    }
}

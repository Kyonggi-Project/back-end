package org.project.simproject.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;
import java.nio.file.attribute.UserPrincipalNotFoundException;

@ControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({IllegalArgumentException.class})
    public <T> ResponseEntity<T> handleFileException() {
        String str = "server Error";
        log.error(str);
        return (ResponseEntity<T>) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(str);
    }

    @ExceptionHandler({UserPrincipalNotFoundException.class})
    public <T> ResponseEntity<T> handleUserException() {
        String str = "Unauthorized Error";
        log.error(str);
        return (ResponseEntity<T>) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(str);
    }
}

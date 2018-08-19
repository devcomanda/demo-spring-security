package com.devcomanda.demospringsecurity.web;

import com.devcomanda.demospringsecurity.exceptions.InternalServerException;
import com.devcomanda.demospringsecurity.exceptions.ValidationNewUserReqException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleCommonException(final RuntimeException ex, final WebRequest request) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Catch Runtime Exception: ", ex);
        } else {
            this.log.error("Catch Runtime Exception with message: {}", ex.getMessage());
        }

        String bodyOfResponse = "Something went wrong :(\n Message:\n " +
                ex.getMessage();

        return this.handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InternalServerException.class)
    protected ResponseEntity<Object> handleInternalServerError(final InternalServerException ex, final WebRequest request) {

        if (this.log.isDebugEnabled()) {
            this.log.debug("Catch Internal Server Exception: ", ex);
        } else {
            this.log.error("Catch Internal Server Exception with message: {}", ex.getMessage());
        }

        String bodyOfResponse = "We had some server error :( \n Message: \n" +
                ex.getMessage();

        return this.handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(ValidationNewUserReqException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final ValidationNewUserReqException ex,
            final WebRequest request
    ) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("User has invalid fields: ", ex);
        } else {
            this.log.error("User has invalid fields: {}", ex.getMessage());
        }
        String bodyOfResponse = "User has invalid fields: :(  " +
                Optional.of(ex.getMessage()).orElse("");

        return this.handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}

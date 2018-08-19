package com.devcomanda.demospringsecurity.exceptions;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class InternalServerException extends RuntimeException {
    public InternalServerException(final String message) {
        super(message);
    }
}

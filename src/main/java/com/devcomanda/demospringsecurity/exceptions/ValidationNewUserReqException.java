package com.devcomanda.demospringsecurity.exceptions;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class ValidationNewUserReqException extends RuntimeException {
    public ValidationNewUserReqException(final String errors) {
        super(errors);
    }
}

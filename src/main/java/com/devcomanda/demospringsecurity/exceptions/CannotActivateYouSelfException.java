package com.devcomanda.demospringsecurity.exceptions;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class CannotActivateYouSelfException extends RuntimeException {
    public CannotActivateYouSelfException() {
        super("You cannot activate or deactivate yourself");
    }
}

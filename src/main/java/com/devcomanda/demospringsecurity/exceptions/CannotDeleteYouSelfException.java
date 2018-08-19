package com.devcomanda.demospringsecurity.exceptions;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class CannotDeleteYouSelfException extends RuntimeException {
    public CannotDeleteYouSelfException() {
        super("You cannot delete yourself");
    }
}

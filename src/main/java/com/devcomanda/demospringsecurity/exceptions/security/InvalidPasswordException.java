package com.devcomanda.demospringsecurity.exceptions.security;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Incorrect password");
    }
}

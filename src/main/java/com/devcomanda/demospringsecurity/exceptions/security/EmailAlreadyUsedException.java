package com.devcomanda.demospringsecurity.exceptions.security;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException() {
        super("Email already in use");
    }
}

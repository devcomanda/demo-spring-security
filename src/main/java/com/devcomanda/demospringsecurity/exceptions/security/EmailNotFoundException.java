package com.devcomanda.demospringsecurity.exceptions.security;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException() {
        super("Email address not registered");
    }
}

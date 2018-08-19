package com.devcomanda.demospringsecurity.exceptions.security;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User not found");
    }
}

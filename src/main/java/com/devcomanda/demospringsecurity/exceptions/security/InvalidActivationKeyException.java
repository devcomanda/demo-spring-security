package com.devcomanda.demospringsecurity.exceptions.security;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class InvalidActivationKeyException extends RuntimeException {
    public InvalidActivationKeyException() {
        super("Invalid activation key");
    }
}

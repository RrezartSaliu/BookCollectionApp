package com.example.bookcollection.model.Exceptions;

public class InvalidUserCredentialsException extends RuntimeException{
    public InvalidUserCredentialsException() {
        super("Invalid user credentials exception");
    }
}

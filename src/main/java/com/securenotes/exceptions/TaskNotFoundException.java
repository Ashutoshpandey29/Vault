package com.securenotes.exceptions;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String msg) {
        super(msg);
    }
}

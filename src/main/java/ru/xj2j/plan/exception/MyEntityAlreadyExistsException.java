package ru.xj2j.plan.exception;

public class MyEntityAlreadyExistsException extends RuntimeException {
    public MyEntityAlreadyExistsException(String message) {
        super(message);
    }
}

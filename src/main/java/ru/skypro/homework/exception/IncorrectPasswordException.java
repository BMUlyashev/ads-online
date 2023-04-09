package ru.skypro.homework.exception;

public class IncorrectPasswordException extends RuntimeException {
    private final String message;

    public IncorrectPasswordException(String message) {
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }
}

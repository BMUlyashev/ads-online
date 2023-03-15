package ru.skypro.homework.exception;

public class UserForbiddenException extends RuntimeException {
    private final int id;

    public UserForbiddenException(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

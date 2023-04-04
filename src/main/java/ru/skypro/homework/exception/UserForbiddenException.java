package ru.skypro.homework.exception;
/**
 * Exception - нет доступа для пользователя
 */
public class UserForbiddenException extends RuntimeException {
    private final int id;

    public UserForbiddenException(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

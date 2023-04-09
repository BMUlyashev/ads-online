package ru.skypro.homework.exception;
/**
 * Exception - пользователь не найден
 */
public class UserNotFoundException extends RuntimeException{
    private final int id;

    public UserNotFoundException(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

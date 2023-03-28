package ru.skypro.homework.exception;

public class AvatarNotFoundException extends RuntimeException {
    private final int id;

    public AvatarNotFoundException(Integer id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

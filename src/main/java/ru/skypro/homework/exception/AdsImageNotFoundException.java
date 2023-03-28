package ru.skypro.homework.exception;

public class AdsImageNotFoundException extends RuntimeException {
    private final int id;

    public AdsImageNotFoundException(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

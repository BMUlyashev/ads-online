package ru.skypro.homework.exception;
/**
 * Exception - Объявление не найдено
 */
public class AdsNotFoundException extends RuntimeException {
    private final int id;

    public AdsNotFoundException(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

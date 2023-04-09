package ru.skypro.homework.exception;
/**
 * Exception - Изображение объявления не найдено
 */
public class AdsImageNotFoundException extends RuntimeException {
    private final int id;

    public AdsImageNotFoundException(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

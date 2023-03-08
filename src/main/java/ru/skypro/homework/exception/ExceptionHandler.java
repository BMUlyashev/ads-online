package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(AdsNotFoundException.class)
    public ResponseEntity<String> handleAdsNotFoundException(AdsNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(String.format("Объявление c id = %d  не найдено", e.getId()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UserNotRegisterException.class)
    public ResponseEntity<String> handleUserNotRegisterException(UserNotRegisterException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(String.format("Пользователь %s не зарегистрирован", e.getEmail()));
    }
}
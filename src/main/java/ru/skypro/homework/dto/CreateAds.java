package ru.skypro.homework.dto;

import lombok.Data;

/**
 * DTO при создании объявления
 */
@Data
public class CreateAds {
    private String description;
    private Integer price;
    private  String title;
}

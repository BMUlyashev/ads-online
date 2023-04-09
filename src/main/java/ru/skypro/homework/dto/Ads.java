package ru.skypro.homework.dto;

import lombok.Data;

/**
 *  DTO сокращенного объявления
 */
@Data
public class Ads {
    private Integer author;
    private String image;
    private Integer pk;
    private Integer price;
    private String title;

}

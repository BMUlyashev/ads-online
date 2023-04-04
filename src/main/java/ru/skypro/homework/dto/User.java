package ru.skypro.homework.dto;

import lombok.Data;

/**
 * DTO пользователя
 */
@Data
public class User {

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String image;
}

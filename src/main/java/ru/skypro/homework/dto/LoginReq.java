package ru.skypro.homework.dto;

import lombok.Data;

/**
 * DTO для логина
 */
@Data
public class LoginReq {
    private String password;
    private String username;

}

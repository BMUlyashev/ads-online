package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class SecurityUserDto {
    private String email;
    private String password;
    private Role role;
}

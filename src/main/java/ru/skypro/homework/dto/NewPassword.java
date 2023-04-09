package ru.skypro.homework.dto;

import lombok.Data;
/**
 * DTO для изменения пароля
 */
@Data
public class NewPassword {
    private String currentPassword;
    private String newPassword;
}

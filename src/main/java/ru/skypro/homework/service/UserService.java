package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.User;

/**
 * Интерфейс для работы с пользователями
 */
public interface UserService {


    /**
     * Получение пользователя
     * @param authentication - аутентификация пользователя;
     * @return Пользователь
     */
    User getUsers(Authentication authentication);

    /**
     * Обновление пользователя
     * @param user Новые параметры пользователя;
     * @param authentication - аутентификация пользователя;
     * @return Обновленный пользователь
     */
    User updateUser(User user, Authentication authentication);

    /**
     * Установка нового пароля
     * @param newPassword - новый пароль;
     * @param authentication - аутентификация пользователя;
     * @return новый пароль
     */
    NewPassword setPassword(NewPassword newPassword, Authentication authentication);

    /**
     * Обновление аватарки пользователя
     * @param image - новая аватарка;
     * @return аватарка
     */
    byte[] updateUserImage (MultipartFile image);
}

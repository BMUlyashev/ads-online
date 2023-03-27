package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.User;

public interface UserService {

    /**
     * получить пользователя
     * @param
     */
    User getUsers(Authentication authentication);

    /**
     * обновить пользователя
     * @param
     */
    User updateUser(User user, Authentication authentication);

    /**
     * установить новый пароль пользователя
     * @param
     */
    NewPassword setPassword(NewPassword newPassword, Authentication authentication);

    /**
     * обновить фото пользователя
     * @param
     */
    byte[] updateUserImage (MultipartFile image);
}

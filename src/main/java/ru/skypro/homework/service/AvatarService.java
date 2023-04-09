package ru.skypro.homework.service;



import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
/**
 * Интерфейс для работы с аватарками пользователя
 */
public interface AvatarService {
    /**
     * Загрузка аватарки пользователя
     * @param image - аватарка;
     * @param authentication - аутентификация;
     * @throws IOException - ошибка загрузки
     */
    void uploadAvatar(MultipartFile image, Authentication authentication) throws IOException;

    /**
     * Получение аватарки пользователя
     * @param id - id аватарки;
     * @return - аватарка;
     * @throws IOException - ошибка загрузки
     */
    Pair<String,byte[]> readAvatar(Integer id) throws IOException;
}

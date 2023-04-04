package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.AdsEntity;
import ru.skypro.homework.model.AdsImage;

import java.io.IOException;

/**
 * Интерфейс для работы с изображениями объявления
 */
public interface AdsImageService {
    /**
     * Загрузка изображения для объявления
     * @param image - изображение;
     * @return - загруженное изображение;
     * @throws IOException - ошибка загрузки
     */
    AdsImage createAdsImage(MultipartFile image) throws IOException;

    /**
     * Обновление изображения объявления
     *
     * @param adsId          - id объявления
     * @param image          - изображение;
     * @param authentication - аутентификация пользователя;
     * @throws IOException
     */
    void updateAdsImage(Integer adsId, MultipartFile image, Authentication authentication) throws IOException;

    /**
     * Получение изображения объявления
     *
     * @param adsId - id изображения;
     * @return - изображение;
     * @throws IOException - ошибка загрузки
     */
    Pair<String, byte[]> getAdsImage(Integer adsId) throws IOException;

    /**
     * Удаление изображения объявления
     *
     * @param id - id изображения
     */
    void deleteImage(Integer id);
}

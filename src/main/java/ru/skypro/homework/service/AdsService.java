package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateAds;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.dto.ResponseWrapperAds;
import ru.skypro.homework.exception.AdsNotFoundException;

import java.io.IOException;
/**
 * Интерфейс для работы с объявлениями
 */
public interface AdsService {

    /**
     * Добавление нового объявление
     * @param properties     {@link CreateAds}
     * @param image          - изображение;
     * @param authentication - аутентификация пользователя;
     * @return Созданное объявление
     */
    Ads addAds(CreateAds properties, MultipartFile image, Authentication authentication) throws IOException;

    /**
     * Удаляет запись из БД по id
     * @param id - id объявления;
     * @throws AdsNotFoundException - Объявление не найдено
     */
    void deleteAds(Integer id, Authentication authentication);

    /**
     * Обновления объявления
     * @param id - id объявления;
     * @param createAds - обновленные данные объявления;
     * @return - обновленное объявление
     */
    Ads updateAds(Integer id, CreateAds createAds, Authentication authentication);

    /**
     * Запрос полного объявления по id
     * @param id - id объявления;
     * @return полное объявление
     */
    FullAds getFullAds(Integer id);

    /**
     * Запрос всех объявлений
     * @return список всех объявлений
     */
    ResponseWrapperAds getAllAds();

    /**
     * Возвращает объявления конкретного пользователя
     * @param authentication - аутентификация пользователя;
     * @return список объявлений конкретного пользователя
     */
    ResponseWrapperAds getAdsMe(Authentication authentication);

    /**
     * Возвращает список объявлений по фильтру
     * @return список объявлений
     */
    ResponseWrapperAds getAllAdsFilter(String filter);
}

package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.model.AdsEntity;
import ru.skypro.homework.model.UserEntity;

@Component
public class UserValidatePermission {

    /**
     * Проверяет, имеет ли user права администратора
     *
     * @param user Сущность {@link UserEntity} пользователя
     * @return true - если пользователь имеет права ADMIN;
     */
    public boolean isAdmin(UserEntity user) {
        return user.getRole().equals(Role.ADMIN);
    }

    /**
     * Проверяет соответствие объявления и автора
     *
     * @param user Сущность {@link UserEntity} пользователя
     * @param ads  Сущность {@link AdsEntity} объявления
     * @return true - если user автор объявления ads
     */
    public boolean isAdsOwner(UserEntity user, AdsEntity ads) {
        return ads.getAuthor().equals(user);
    }
}

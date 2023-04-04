package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.model.AdsEntity;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.service.AdsImageService;

/**
 * Сервис валидации пользователя
 */
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

    /**
     * Проверяет соответствие комментария и автора
     *
     * @param user    Сущность {@link UserEntity} пользователя
     * @param comment Сущность {@link CommentEntity} комментария
     * @return true - если user автор комментария comment
     */
    public boolean isCommentOwner(UserEntity user, CommentEntity comment) {
        return comment.getUser().equals(user);
    }
}

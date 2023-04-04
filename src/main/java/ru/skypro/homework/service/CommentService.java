package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.ResponseWrapperComment;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.AdsNotFoundException;
/**
 * Интерфейс для работы с комментариями пользователя
 */
public interface CommentService {
    /**
     * Добавление нового комментария к объявлению.
     *
     * @param id             - id объявления;
     * @param comment        - {@link Comment};
     * @param authentication - аутентификация пользователя;
     * @return Созданный комментарий;
     * @throws AdsNotFoundException - Объявление не найдено
     */
    Comment addComment(Integer id, Comment comment, Authentication authentication);

    /**
     * Обновление комментария.
     *
     * @param adId      - id объявления;
     * @param commentId - id комментария;
     * @param comment   - {@link Comment};
     * @return Обновленный комментарий;
     * @throws CommentNotFoundException - Комментарий не найден
     */
    Comment updateComment(Integer adId, Integer commentId, Comment comment, Authentication authentication);

    /**
     * Получения комментария по id.
     *
     * @param adId - id объявления;
     * @param commentId - id комментария;
     * @return Найденный комментарий;
     * @throws CommentNotFoundException - Комментарий не найден
     */
    Comment getComment(Integer adId, Integer commentId);

    /**
     * Удаление комментария по id.
     *
     * @param adId - id объявления;
     * @param commentId - id комментария;
     * @throws CommentNotFoundException - Комментарий не найден
     */
    void deleteComment(Integer adId, Integer commentId, Authentication authentication);

    /**
     * Получения списка все комментариев к объявлению.
     *
     * @param id - id объявления;
     * @return Список всех комментариев.
     */
    ResponseWrapperComment getAllCommentsByAd(Integer id);
}

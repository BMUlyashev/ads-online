package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.ResponseWrapperComment;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.AdsNotFoundException;

public interface CommentService {
    /**
     * Добавление нового комментария к объявлению.
     *
     * @param id             - id объявления;
     * @param comment        - {@link Comment};
     * @param authentication - аутентификация пользователя;
     * @return Созданный комментарий;
     * @throws AdsNotFoundException
     */
    Comment addComment(Integer id, Comment comment, Authentication authentication);

    /**
     * Обновление комментария.
     *
     * @param adId      - id объявления;
     * @param commentId - id комментария;
     * @param comment   - {@link Comment};
     * @return Обновленный комментарий;
     * @throws CommentNotFoundException
     */
    Comment updateComment(Integer adId, Integer commentId, Comment comment, Authentication authentication);

    /**
     * Получения комментария по id.
     *
     * @param adId - id объявления;
     * @param commentId - id комментария;
     * @return Найденый комментарий;
     * @throws CommentNotFoundException
     */
    Comment getComment(Integer adId, Integer commentId);

    /**
     * Удаление комментария по id.
     *
     * @param adId - id объявления;
     * @param commentId - id комментария;
     * @throws CommentNotFoundException
     */
    void deleteComment(Integer adId, Integer commentId);

    /**
     * Получения списка все комментариев к объявлению.
     *
     * @param id - id объявления;
     * @return Список всех комментариев.
     */
    ResponseWrapperComment getAllCommentsByAd(Integer id);
}

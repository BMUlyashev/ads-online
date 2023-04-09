package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.UserAvatar;

/**
 * Репозитория для аватарок пользователей
 */
@Repository
public interface AvatarRepository extends JpaRepository<UserAvatar, Integer> {
}

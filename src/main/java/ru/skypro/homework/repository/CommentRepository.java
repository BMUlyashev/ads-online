package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.CommentEntity;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для комментариев пользователей к объявлениям
 */
@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findAllByAds_Id(Integer id);
    Optional<CommentEntity> findByAds_IdAndId(Integer adsId, Integer id);
}

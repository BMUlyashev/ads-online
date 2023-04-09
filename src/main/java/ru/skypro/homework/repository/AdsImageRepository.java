package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.AdsImage;

/**
 * Репозиторий для изображений объявлений
 */
@Repository
public interface AdsImageRepository extends JpaRepository<AdsImage, Integer> {

}

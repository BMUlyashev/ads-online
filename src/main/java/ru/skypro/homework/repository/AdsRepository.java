package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.AdsEntity;

import java.util.List;

@Repository
public interface AdsRepository extends JpaRepository<AdsEntity, Integer> {
    List<AdsEntity> findAllByAuthor_Id(Integer id);

    List<AdsEntity> findByTitleContainingIgnoreCase(String subTitle);
}

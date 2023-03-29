package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.AdsImage;

import java.util.Optional;

@Repository
public interface AdsImageRepository extends JpaRepository<AdsImage, Integer> {
    Optional<AdsImage> findAdsImageByAdsEntity_Id(Integer id);

    Optional<AdsImage> findByIdAndAdsEntity_Id(Integer id, Integer adsId);
}

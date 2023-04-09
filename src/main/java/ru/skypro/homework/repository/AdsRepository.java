package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.AdsEntity;

import java.util.List;

/**
 * Репозиторий для объявлений
 */
@Repository
public interface AdsRepository extends JpaRepository<AdsEntity, Integer> {
    List<AdsEntity> findAllByAuthor_Id(Integer id);

    @Query(value = "SELECT * FROM ads as a " +
            "LEFT JOIN users as u ON a.author_id = u.id " +
            "LEFT JOIN comments as c ON c.ads_id = a.id " +
            "WHERE a.title ILIKE %:filter% " +
            "OR a.description ILIKE %:filter% " +
            "OR  CAST(a.price as text) ILIKE %:filter% " +
            "OR u.first_name ILIKE %:filter% " +
            "OR u.last_name ILIKE %:filter% " +
            "OR c.text ILIKE %:filter% "
            , nativeQuery = true)
    List<AdsEntity> findAllByFilter(@Param("filter") String filter);
}

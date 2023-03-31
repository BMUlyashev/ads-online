package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.AdsEntity;
import ru.skypro.homework.model.AdsImage;

import java.io.IOException;

public interface AdsImageService {
    AdsImage createAdsImage(MultipartFile image) throws IOException;

    void updateAdsImage(Integer adsId, MultipartFile image, Authentication authentication) throws IOException;

    Pair<String,byte[]> getAdsImage(Integer adsId) throws IOException;

    void deleteImage(Integer id);
}

package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.model.AdsEntity;
import ru.skypro.homework.model.AdsImage;

import java.io.IOException;

public interface AdsImageService {
    AdsImage createAdsImage(MultipartFile image, AdsEntity adsEntity) throws IOException;

    Void updateAdsImage(Integer id, MultipartFile image, Authentication authentication);
}

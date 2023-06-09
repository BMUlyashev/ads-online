package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exception.AdsImageNotFoundException;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.UserForbiddenException;
import ru.skypro.homework.model.AdsEntity;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdsImageRepository;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdsImageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Реализация интерфейса {@link AdsImageService}
 */
@Log
@Service
@RequiredArgsConstructor
public class AdsImageServiceImpl implements AdsImageService {

    @Value("${path.to.image.folder}")
    private String imageFolder;
    private final AdsImageRepository adsImageRepository;
    private final UserRepository userRepository;
    private final AdsRepository adsRepository;
    private final UserValidatePermission validatePermission;


    @Override
    public AdsImage createAdsImage(MultipartFile image) throws IOException {
        log.info("completed createAdsImage");
        AdsImage adsImage = createImage(image);
        adsImage = adsImageRepository.save(adsImage);
        String extension = Optional.ofNullable(image.getOriginalFilename())
                .map(a -> a.substring(a.lastIndexOf(".")))
                .orElse("");
        Path filePath = Paths.get(imageFolder).resolve(adsImage.getId() + extension);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, image.getBytes());
        adsImage.setPath(filePath.toString());
        return adsImageRepository.save(adsImage);
    }

    @Override
    public void updateAdsImage(Integer adsId, MultipartFile image, Authentication authentication) throws IOException {
        log.info("completed updateAdsImage");
        UserEntity user = userRepository.findUserEntityByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + authentication.getName()));
        AdsEntity adsEntity = adsRepository.findById(adsId).orElseThrow(() -> new AdsNotFoundException(adsId));
        if (validatePermission.isAdmin(user) || validatePermission.isAdsOwner(user, adsEntity)) {
            AdsImage adsImage = adsImageRepository.findById(adsEntity.getImage().getId())
                    .orElseThrow(() -> new AdsImageNotFoundException(adsEntity.getImage().getId()));
            Path filePath = Paths.get(adsImage.getPath());
            Files.write(filePath, image.getBytes());
            adsImageRepository.save(adsImage);
        } else {
            throw new UserForbiddenException(user.getId());
        }
    }

    @Override
    public Pair<String, byte[]> getAdsImage(Integer id) throws IOException {
        log.info("completed getAdsImage");
        AdsImage adsImage = adsImageRepository.findById(id)
                .orElseThrow(() -> new AdsImageNotFoundException(id));
        return Pair.of(adsImage.getMediaType(), Files.readAllBytes(Paths.get(adsImage.getPath())));
    }

    @Override
    public void deleteImage(Integer id) {
        log.info("completed deleteImage");
        AdsImage findImage = adsImageRepository.findById(id)
                .orElseThrow(() -> new AdsImageNotFoundException(id));
        File deleteFile = Paths.get(findImage.getPath()).toFile();
        deleteFile.delete();
        adsImageRepository.deleteById(id);
    }


    private AdsImage createImage(MultipartFile image) {
        log.info("completed createImage");
        AdsImage adsImage = new AdsImage();
        adsImage.setMediaType(image.getContentType());
        adsImage.setFileSize(image.getSize());
        return adsImageRepository.save(adsImage);
    }
}

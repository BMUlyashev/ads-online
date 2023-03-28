package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdsImageServiceImpl implements AdsImageService {

    @Value(value = "${path.to.images.folder}")
    private String imageFolder;
    private final AdsImageRepository adsImageRepository;
    private final UserRepository userRepository;
    private final AdsRepository adsRepository;
    private final UserValidatePermission validatePermission;


    @Override
    public AdsImage createAdsImage(MultipartFile image, AdsEntity adsEntity) throws IOException {
        AdsImage adsImage = createImage(image);
        String extension = Optional.ofNullable(image.getOriginalFilename())
                .map(a -> a.substring(a.lastIndexOf(".")))
                .orElse("");
        Path filePath = Path.of(imageFolder, adsImage.getId() + extension);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, image.getBytes());
        adsImage.setAdsEntity(adsEntity);
        adsImage.setPath(filePath.toString());
        return adsImageRepository.save(adsImage);
    }

    @Override
    public Void updateAdsImage(Integer id, MultipartFile image, Authentication authentication) {
        UserEntity user = userRepository.findUserEntityByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + authentication.getName()));
        AdsEntity adsEntity = adsRepository.findById(id).orElseThrow(() -> new AdsNotFoundException(id));
        if (validatePermission.isAdmin(user) || validatePermission.isAdsOwner(user, adsEntity)) {
         adsImageRepository.findAdsImageByAdsEntity_Id(id)
                    .orElseThrow(() -> new AdsImageNotFoundException(id));
        } else {
            throw new UserForbiddenException(user.getId());
        }
    }

    private AdsImage createImage(MultipartFile image) {
        AdsImage adsImage = new AdsImage();
        adsImage.setMediaType(image.getContentType());
        adsImage.setFileSize(image.getSize());
        return adsImageRepository.save(adsImage);
    }
}

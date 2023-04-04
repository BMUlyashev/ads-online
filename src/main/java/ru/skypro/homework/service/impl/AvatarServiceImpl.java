package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exception.AvatarNotFoundException;
import ru.skypro.homework.exception.UserNotRegisterException;
import ru.skypro.homework.model.UserAvatar;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AvatarRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdsImageService;
import ru.skypro.homework.service.AvatarService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
/**
 * Реализация интерфейса {@link AvatarService}
 */
@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    @Value("${path.to.avatars.folder}")
    private String avatarFolder;
    private final UserRepository userRepository;
    private final AvatarRepository avatarRepository;

    @Override
    public void uploadAvatar(MultipartFile image, Authentication authentication) throws IOException {
        UserEntity user = userRepository.findUserEntityByEmail(authentication.getName()).orElseThrow(
                () -> new UserNotRegisterException(authentication.getName())
        );
        UserAvatar avatar;
        if (user.getAvatar() == null) {
            avatar = createAvatar(image);
        } else {
            avatar = user.getAvatar();
        }

        String extension = Optional.ofNullable(image.getOriginalFilename())
                .map(a -> a.substring(a.lastIndexOf(".")))
                .orElse("");
        Path path = Paths.get(avatarFolder).resolve(avatar.getId() + extension);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());
        avatar.setFilePath(path.toString());
        avatarRepository.save(avatar);
        user.setAvatar(avatar);
        userRepository.save(user);
    }

    @Override
    public Pair<String, byte[]> readAvatar(Integer id) throws IOException {
        UserAvatar avatar = avatarRepository.findById(id).orElseThrow(
                () -> new AvatarNotFoundException(id)
        );
        return Pair.of(avatar.getMediaType(), Files.readAllBytes(Paths.get(avatar.getFilePath())));
    }

    private UserAvatar createAvatar(MultipartFile avatarFile) {
        UserAvatar avatar = new UserAvatar();
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setFileSize(avatarFile.getSize());
        return avatarRepository.save(avatar);
    }
}

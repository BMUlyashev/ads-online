package ru.skypro.homework.service;



import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AvatarService {
    void uploadAvatar(MultipartFile image, Authentication authentication) throws IOException;

    Pair<String,byte[]> readAvatar(Integer id) throws IOException;
}

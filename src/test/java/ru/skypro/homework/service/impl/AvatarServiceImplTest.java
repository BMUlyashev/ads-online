package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exception.AvatarNotFoundException;
import ru.skypro.homework.model.UserAvatar;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AvatarRepository;
import ru.skypro.homework.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
/**
 * Тесты для {@link AvatarServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
class AvatarServiceImplTest {

    @InjectMocks
    AvatarServiceImpl avatarService;

    @Mock
    UserRepository userRepository;

    @Mock
    AvatarRepository avatarRepository;

    @Mock
    Authentication authentication;


    @Test
    void uploadAvatarUserNotFoundException() throws URISyntaxException, IOException {
        Path path = Paths.get(AvatarServiceImpl.class.getResource("test.gif").toURI());

        MultipartFile multipartFile = new MockMultipartFile("file",
                "test.gif", "image/gif", Files.readAllBytes(path));


        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("test@mail.com");

        assertThatThrownBy(() -> avatarService.uploadAvatar(multipartFile, authentication)).isInstanceOf(
                UsernameNotFoundException.class
        );
    }

    @Test
    void uploadAvatarUserNotHaveAvatar() throws URISyntaxException, IOException {
        Path path = Paths.get(AvatarServiceImpl.class.getResource("test.gif").toURI());
        String imageNewPath = path.getParent().toString();
        UserEntity user = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        UserEntity userWithAvatar = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        MultipartFile multipartFile = new MockMultipartFile("file",
                "test.gif", "image/gif", Files.readAllBytes(path));
        ReflectionTestUtils.setField(avatarService, "avatarFolder", imageNewPath);
        UserAvatar avatar = createAvatar(multipartFile, 1);
        UserAvatar avatarNew = createAvatar(multipartFile, 1);
        avatarNew.setFilePath(imageNewPath + "/1.gif");
        userWithAvatar.setAvatar(avatarNew);

        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(avatarRepository.save(any(UserAvatar.class)))
                .thenReturn(avatar)
                .thenReturn(avatarNew);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        avatarService.uploadAvatar(multipartFile, authentication);

        File actualFile = new File(imageNewPath + "/1.gif");
        File expectedFile = new File(imageNewPath + "/test.gif");
        assertThat(actualFile).hasSameBinaryContentAs(expectedFile);
        actualFile.delete();
    }

    @Test
    void uploadAvatarUserHaveAvatar() throws URISyntaxException, IOException {
        Path path = Paths.get(AvatarServiceImpl.class.getResource("test.gif").toURI());
        String imageNewPath = path.getParent().toString();
        UserEntity user = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        MultipartFile multipartFile = new MockMultipartFile("file",
                "test.gif", "image/gif", Files.readAllBytes(path));
        ReflectionTestUtils.setField(avatarService, "avatarFolder", imageNewPath);
        UserAvatar avatar = createAvatar(multipartFile, 3);
        avatar.setFilePath(imageNewPath + "/3.gif");
        user.setAvatar(avatar);

        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(avatarRepository.save(any(UserAvatar.class)))
                .thenReturn(avatar);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        avatarService.uploadAvatar(multipartFile, authentication);

        File actualFile = new File(imageNewPath + "/3.gif");
        File expectedFile = new File(imageNewPath + "/test.gif");
        assertThat(actualFile).hasSameBinaryContentAs(expectedFile);
        actualFile.delete();
    }

    @Test
    void readAvatarNotFoundException() {
        when(avatarRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> avatarService.readAvatar(1)).isInstanceOf(AvatarNotFoundException.class);
    }

    @Test
    void readAvatar() throws IOException, URISyntaxException {
        Path path = Paths.get(AvatarServiceImpl.class.getResource("11.gif").toURI());

        MultipartFile multipartFile = new MockMultipartFile("file",
                "test.gif", "image/gif", Files.readAllBytes(path));
        UserAvatar avatar = createAvatar(multipartFile, 11);
        avatar.setFilePath(path.toString());
        Pair<String, byte[]> expected = Pair.of(avatar.getMediaType(), multipartFile.getBytes());
        when(avatarRepository.findById(any()))
                .thenReturn(Optional.of(avatar))
                .thenReturn(Optional.empty());

        assertThat(avatarService.readAvatar(11)).isEqualTo(expected);
        assertThatThrownBy(() -> avatarService.readAvatar(2)).isInstanceOf(AvatarNotFoundException.class);
    }


    private UserAvatar createAvatar(MultipartFile avatarFile, Integer id) throws IOException {
        UserAvatar avatar = new UserAvatar();
        avatar.setId(id);
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setFileSize(avatarFile.getSize());
        return avatar;
    }

    private UserEntity createUser(Integer id, String firstName, String lastName, String email, String phone) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        return user;
    }
}
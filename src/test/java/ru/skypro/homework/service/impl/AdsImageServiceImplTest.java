package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exception.AdsImageNotFoundException;
import ru.skypro.homework.exception.UserForbiddenException;
import ru.skypro.homework.model.AdsEntity;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdsImageRepository;
import ru.skypro.homework.repository.AdsRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdsImageServiceImplTest {

    @Mock
    AdsImageRepository adsImageRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    AdsRepository adsRepository;
    @Mock
    UserValidatePermission userValidatePermission;
    @Mock
    Authentication authentication;
    @InjectMocks
    AdsImageServiceImpl out;



    @Test
    void createAdsImage() throws IOException, URISyntaxException {
        Path path = Paths.get(AdsImageServiceImpl.class.getResource("test.gif").toURI());


        String imagePath = path.getParent().toString();
        MultipartFile multipartFile = new MockMultipartFile("file",
                "test.gif", "image/gif", Files.readAllBytes(path));
        MultipartFile multipartFile2 = new MockMultipartFile("file",
                "1.gif", "image/gif", Files.readAllBytes(path));
        AdsEntity adsEntity = new AdsEntity(1, "Test", "Test2", 100);
        AdsImage image = createImage(multipartFile);

        when(adsImageRepository.save(any())).thenReturn(image);
        ReflectionTestUtils.setField(out, "imageFolder", imagePath);
        AdsImage actual = out.createAdsImage(multipartFile, adsEntity);

        Path path2 = Paths.get(AdsImageServiceImpl.class.getResource("1.gif").toURI());
        AdsImage expected = createImage(multipartFile2);
        expected.setPath(path2.toString());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateAdsImage() throws URISyntaxException, IOException {
        Path path = Paths.get(AdsImageServiceImpl.class.getResource("test.gif").toURI());
        String imageNewPath = path.getParent().toString();
        MultipartFile multipartFile = new MockMultipartFile("file",
                "test.gif", "image/gif", Files.readAllBytes(path));
        ReflectionTestUtils.setField(out, "imageFolder", imageNewPath);
        AdsEntity adsEntity = new AdsEntity(1, "Test", "Test2", 100);
        AdsImage image = createImage(multipartFile);
        adsEntity.setImage(image);
        image.setPath(imageNewPath + "/3.gif");
        UserEntity user = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(adsRepository.findById(any())).thenReturn(Optional.of(adsEntity));
        when(userValidatePermission.isAdmin(any())).thenReturn(true);
        when(adsImageRepository.findById(any())).thenReturn(Optional.of(image));
        out.updateAdsImage(1, multipartFile, authentication);

        File actualFile = new File(imageNewPath + "/3.gif");
        File expectedFile = new File(imageNewPath + "/test.gif");
        assertThat(actualFile).hasSameBinaryContentAs(expectedFile);
        actualFile.delete();
    }

    @Test
    void updateAdsImageForbiddenException() {
        AdsEntity adsEntity = new AdsEntity(1, "Test", "Test2", 100);
        UserEntity user = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(adsRepository.findById(any())).thenReturn(Optional.of(adsEntity));
        when(userValidatePermission.isAdmin(any())).thenReturn(false);
        when(userValidatePermission.isAdsOwner(user, adsEntity)).thenReturn(false);
        assertThatThrownBy(() -> out.updateAdsImage(1, null, authentication))
                .isInstanceOf(UserForbiddenException.class);
    }

    @Test
    void getAdsImage() throws URISyntaxException, IOException {
        Path path = Paths.get(AdsImageServiceImpl.class.getResource("1.gif").toURI());
        MultipartFile multipartFile = new MockMultipartFile("file",
                "test.gif", "image/gif", Files.readAllBytes(path));
        AdsEntity adsEntity = new AdsEntity(1, "Test", "Test2", 100);
        AdsImage image = createImage(multipartFile);
        adsEntity.setImage(image);
        image.setPath(path.toString());
        when(adsImageRepository.findById(any())).thenReturn(java.util.Optional.of(image));
        Pair<String, byte[]> expected = Pair.of(image.getMediaType(), multipartFile.getBytes());
        Pair<String, byte[]> actual = out.getAdsImage(1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAdsImageNotFound() {
        when(adsImageRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> out.getAdsImage(11)).isInstanceOf(AdsImageNotFoundException.class);
    }

    @Test
    void deleteImage() throws IOException, URISyntaxException {
        Path path = Paths.get(AdsImageServiceImpl.class.getResource("1.gif").toURI());
        MultipartFile multipartFile = new MockMultipartFile("file",
                "test.gif", "image/gif", Files.readAllBytes(path));
        AdsEntity adsEntity = new AdsEntity(1, "Test", "Test2", 100);
        AdsImage image = createImage(multipartFile);
        adsEntity.setImage(image);
        image.setPath(path.toString());
        when(adsImageRepository.findById(any())).thenReturn(java.util.Optional.of(image));
        out.deleteImage(1);
        verify(adsImageRepository, times(1)).deleteById(1);
    }

    private AdsImage createImage(MultipartFile image) {
        AdsImage adsImage = new AdsImage();
        adsImage.setId(1);
        adsImage.setMediaType(image.getContentType());
        adsImage.setFileSize(image.getSize());
        return adsImage;
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
package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.UserForbiddenException;
import ru.skypro.homework.exception.UserNotRegisterException;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.model.AdsEntity;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.UserRepository;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdsServiceImplTest {

    @Mock
    AdsRepository adsRepository;

    @Mock
    UserRepository userRepository;
    @Mock
    AdsImageServiceImpl adsImageService;

    @Mock
    Authentication authentication;

    @InjectMocks
    AdsServiceImpl adsService;

    @Spy
    AdsMapper adsMapper = Mappers.getMapper(AdsMapper.class);

    @Spy
    UserValidatePermission validatePermission = new UserValidatePermission();

    @Test
//    @WithMockUser(username = "user@gmail.com", password = "password", roles = "USER")
    void addAds() throws URISyntaxException, IOException {
        CreateAds ads = createDtoCreateAds("testDescription", "testTitle", 100);
        AdsEntity adsEntity = createAdsEntity(2, "testDescription", "testTitle", 100);
        UserEntity user = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        adsEntity.setAuthor(user);
        Path path = Paths.get(AdsServiceImpl.class.getResource("11.gif").toURI());
        String imagePath = path.getParent().toString();
        MultipartFile image = new MockMultipartFile("file",
                "test.gif", "image/gif", Files.readAllBytes(path));
        AdsImage adsImage = new AdsImage(1,imagePath, 10L, "png" );
        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findUserEntityByEmail(any(String.class)))
                .thenReturn(Optional.of(user));
        when(adsRepository.save(any(AdsEntity.class))).thenReturn(adsEntity);
        when(adsImageService.createAdsImage(image, adsEntity)).thenReturn(adsImage);

        Ads result = adsService.addAds(ads, image, authentication);

        assertThat(result.getAuthor()).isEqualTo(1);
        assertThat(result.getPk()).isEqualTo(2);
        assertThat(result.getPrice()).isEqualTo(100);
        assertThat(result.getTitle()).isEqualTo("testTitle");
        assertThat(result.getImage()).isEqualTo("/ads/me/image/1");
    }

    @Test
    void addAdsThrowException() {
        CreateAds ads = createDtoCreateAds("testDescription", "testTitle", 100);
        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findUserEntityByEmail(any(String.class)))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> adsService.addAds(ads, null, authentication)).isInstanceOf(UserNotRegisterException.class);
    }

    @Test
    void deleteAdsIfUserOwner() {
        AdsEntity adsEntity = createAdsEntity(2, "testDescription", "testTitle", 100);
        UserEntity user = createUser(1, "test", "test", "test@mail.com", "1");
        user.setRole(Role.USER);
        adsEntity.setAuthor(user);
        when(adsRepository.findById(1)).thenReturn(Optional.of(adsEntity));
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("test@test.com");
        doNothing().when(adsRepository).deleteById(1);
        adsService.deleteAds(1, authentication);
        verify(adsRepository).deleteById(1);
    }

    @Test
    void deleteAdsIfUserAdmin() {
        AdsEntity adsEntity = createAdsEntity(2, "testDescription", "testTitle", 100);
        UserEntity user = createUser(1, "test", "test", "test@mail.com", "1");
        user.setRole(Role.ADMIN);
        when(adsRepository.findById(1)).thenReturn(Optional.of(adsEntity));
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("test@test.com");
        doNothing().when(adsRepository).deleteById(1);
        adsService.deleteAds(1, authentication);
        verify(adsRepository).deleteById(1);
    }

    @Test
    void deleteAdsThrowExceptionAdsNotFound() {
        UserEntity user = createUser(1, "test", "test", "test@mail.com", "1");

        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(adsRepository.findById(1)).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("test@test.com");
        assertThatThrownBy(() -> adsService.deleteAds(1, authentication)).isInstanceOf(AdsNotFoundException.class);
    }

    @Test
    void deleteAdsThrowExceptionUserNotFound() {
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("test@test.com");
        assertThatThrownBy(() -> adsService.deleteAds(2, authentication)).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void deleteAdsForbiddenUserException() {
        AdsEntity adsEntity = createAdsEntity(2, "testDescription", "testTitle", 100);
        UserEntity user = createUser(1, "test", "test", "test@mail.com", "1");
        UserEntity owner = createUser(2, "owner", "owner", "owner@mail.com", "2");
        user.setRole(Role.USER);
        adsEntity.setAuthor(owner);
        when(adsRepository.findById(2)).thenReturn(Optional.of(adsEntity));
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("test@test.com");

        assertThatThrownBy(() -> adsService.deleteAds(2, authentication)).isInstanceOf(UserForbiddenException.class);
    }

    @Test
    void updateAdsIfUserOwner() {
        AdsEntity adsEntity = createAdsEntity(1, "testDescription", "testTitle", 100);
        UserEntity owner = createUser(2, "owner", "owner", "owner@mail.com", "2");
        owner.setRole(Role.USER);
        adsEntity.setAuthor(owner);
        CreateAds ads = createDtoCreateAds("testDescriptionNew", "testTitleNew", 200);
        AdsEntity adsEntityNew = createAdsEntity(1, "testDescriptionNew", "testTitleNew", 200);

        when(authentication.getName()).thenReturn("owner@mail.com");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(owner));
        when(adsRepository.findById(1)).thenReturn(Optional.of(adsEntity));
        when(adsRepository.save(any(AdsEntity.class))).thenReturn(adsEntityNew);

        Ads result = adsService.updateAds(1, ads, authentication);
        assertThat(result.getPrice()).isEqualTo(200);
        assertThat(result.getTitle()).isEqualTo("testTitleNew");
        assertThat(result.getPk()).isEqualTo(1);
    }

    @Test
    void updateAdsIfUserAdmin() {
        AdsEntity adsEntity = createAdsEntity(1, "testDescription", "testTitle", 100);
        UserEntity owner = createUser(2, "owner", "owner", "owner@mail.com", "2");
        UserEntity admin = createUser(1, "admin", "admin", "admin@mail.com", "2");
        admin.setRole(Role.ADMIN);
        adsEntity.setAuthor(owner);
        CreateAds ads = createDtoCreateAds("testDescriptionNew", "testTitleNew", 200);
        AdsEntity adsEntityNew = createAdsEntity(1, "testDescriptionNew", "testTitleNew", 200);

        when(authentication.getName()).thenReturn("admin@mail.com");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(admin));
        when(adsRepository.findById(1)).thenReturn(Optional.of(adsEntity));
        when(adsRepository.save(any(AdsEntity.class))).thenReturn(adsEntityNew);

        Ads result = adsService.updateAds(1, ads, authentication);
        assertThat(result.getPrice()).isEqualTo(200);
        assertThat(result.getTitle()).isEqualTo("testTitleNew");
        assertThat(result.getPk()).isEqualTo(1);
    }

    @Test
    void updateAdsThrowExceptionAdsNotFound() {
        CreateAds ads = createDtoCreateAds("testDescriptionNew", "testTitleNew", 200);
        UserEntity owner = createUser(2, "owner", "owner", "owner@mail.com", "2");

        when(authentication.getName()).thenReturn("owner@mail.com");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(owner));
        when(adsRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adsService.updateAds(1, ads, authentication)).isInstanceOf(AdsNotFoundException.class);
    }

    @Test
    void updateAdsThrowExceptionUserNotFound() {
        CreateAds ads = createDtoCreateAds("testDescriptionNew", "testTitleNew", 200);

        when(authentication.getName()).thenReturn("owner@mail.com");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adsService.updateAds(1, ads, authentication)).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void updateAdsThrowExceptionUserForbidden() {
        CreateAds ads = createDtoCreateAds("testDescriptionNew", "testTitleNew", 200);
        UserEntity user = createUser(1, "user", "user", "user@mail.com", "2");
        UserEntity owner = createUser(2, "owner", "owner", "owner@mail.com", "2");
        AdsEntity adsEntity = createAdsEntity(1, "testDescription", "testTitle", 100);
        user.setRole(Role.USER);
        adsEntity.setAuthor(owner);

        when(adsRepository.findById(1)).thenReturn(Optional.of(adsEntity));
        when(authentication.getName()).thenReturn("user@mail.com");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> adsService.updateAds(1, ads, authentication)).isInstanceOf(UserForbiddenException.class);
    }

    @Test
    void getFullAds() {
        AdsEntity adsEntity = createAdsEntity(1, "testDescription", "testTitle", 100);
        UserEntity user = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        adsEntity.setAuthor(user);
        when(adsRepository.findById(1)).thenReturn(Optional.of(adsEntity));
        FullAds result = adsService.getFullAds(1);

        assertThat(result.getAuthorFirstName()).isEqualTo(user.getFirstName());
        assertThat(result.getAuthorLastName()).isEqualTo(user.getLastName());
        assertThat(result.getDescription()).isEqualTo(adsEntity.getDescription());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getPk()).isEqualTo(adsEntity.getId());
        assertThat(result.getTitle()).isEqualTo(adsEntity.getTitle());
        assertThat(result.getPrice()).isEqualTo(adsEntity.getPrice());
    }

    @Test
    void getFullAdsThrowException() {
        when(adsRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adsService.getFullAds(1)).isInstanceOf(AdsNotFoundException.class);
    }

    @Test
    void getAllAds() {
        List<AdsEntity> adsEntityList = List.of(
                createAdsEntity(1, "1", "1", 1),
                createAdsEntity(2, "2", "2", 2),
                createAdsEntity(3, "3", "3", 3),
                createAdsEntity(4, "4", "4", 4)
        );
        List<Ads> adsList = List.of(
                createAds(1, 1, "1"),
                createAds(2, 2, "2"),
                createAds(3, 3, "3"),
                createAds(4, 4, "4")
        );
        when(adsRepository.findAll()).thenReturn(adsEntityList);
        ResponseWrapperAds expected = new ResponseWrapperAds();
        expected.setResults(adsList);
        ResponseWrapperAds actual = adsService.getAllAds();
        assertThat(actual.getCount()).isEqualTo(4);
        assertThat(actual.getResults()).isEqualTo(expected.getResults());
    }

    @Test
    void getFilterAds() {
        List<AdsEntity> adsEntityList = List.of(
                createAdsEntity(1, "1", "1", 1),
                createAdsEntity(2, "2", "21", 2),
                createAdsEntity(3, "3", "31", 3),
                createAdsEntity(4, "4", "41", 4)
        );
        List<Ads> adsList = List.of(
                createAds(1, 1, "1"),
                createAds(2, 2, "21"),
                createAds(3, 3, "31"),
                createAds(4, 4, "41")
        );
        when(adsRepository.findAllByFilter("1")).thenReturn(adsEntityList);
        ResponseWrapperAds expected = new ResponseWrapperAds();
        expected.setResults(adsList);
        ResponseWrapperAds actual = adsService.getAllAdsFilter("1");
        assertThat(actual.getCount()).isEqualTo(4);
        assertThat(actual.getResults()).isEqualTo(expected.getResults());
    }

    @Test
    void getAdsMe() {
        UserEntity user = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        List<AdsEntity> listAdsEntity = List.of(
                createAdsEntity(1, "1", "1", 100),
                createAdsEntity(2, "2", "2", 200),
                createAdsEntity(3, "3", "3", 300),
                createAdsEntity(4, "4", "4", 400)
        );
        listAdsEntity.forEach(a -> a.setAuthor(user));

        List<Ads> adsList = List.of(
                createAds(1, 100, "1"),
                createAds(2, 200, "2"),
                createAds(3, 300, "3"),
                createAds(4, 400, "4")
        );
        adsList.forEach(a -> a.setAuthor(user.getId()));
        ResponseWrapperAds expected = new ResponseWrapperAds();
        expected.setResults(adsList);
        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(adsRepository.findAllByAuthor_Id(1)).thenReturn(listAdsEntity);

        ResponseWrapperAds actual = adsService.getAdsMe(authentication);

        assertThat(actual.getCount()).isEqualTo(4);
        assertThat(actual.getResults()).filteredOn(a -> a.getAuthor().equals(1)).hasSize(4);
        assertThat(actual.getResults()).isEqualTo(expected.getResults());
    }

    @Test
    void getAdsMeThrowException() {
        when(userRepository.findUserEntityByEmail(any(String.class)))
                .thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("test@test.com");

        assertThatThrownBy(() -> adsService.getAdsMe(authentication)).isInstanceOf(UserNotRegisterException.class);
    }

    private CreateAds createDtoCreateAds(String description, String title, Integer price) {
        CreateAds ads = new CreateAds();
        ads.setDescription(description);
        ads.setTitle(title);
        ads.setPrice(price);
        return ads;
    }

    private AdsEntity createAdsEntity(Integer id, String description, String title, Integer price) {
        AdsEntity ads = new AdsEntity();
        ads.setDescription(description);
        ads.setTitle(title);
        ads.setPrice(price);
        ads.setId(id);
        return ads;
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


    private Ads createAds(Integer pk, Integer price, String title) {
        Ads ads = new Ads();
        ads.setPk(pk);
        ads.setPrice(price);
        ads.setTitle(title);
        return ads;
    }
}
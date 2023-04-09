package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.model.UserAvatar;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AvatarRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AvatarService;
import ru.skypro.homework.service.impl.AvatarServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @SpyBean
    private AvatarService avatarService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AvatarRepository avatarRepository;

    @Test
    @WithMockUser("user")
    void getUsers() throws Exception {
        User expected = createUserDto(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        UserEntity user = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        mvc.perform(MockMvcRequestBuilders
                        .get("/users/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    User actual = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), User.class);
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(actual).usingRecursiveComparison().ignoringFields("image").isEqualTo(expected);
                });
    }

    @Test
    void getUsersUnauthorized() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/users/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                });
    }

    @Test
    @WithMockUser("user")
    void getUsersForbidden() throws Exception {
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders
                        .get("/users/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
                });
    }


    @Test
    @WithMockUser("test")
    void updateUser() throws Exception {
        User expected = createUserDto(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        UserEntity user = createUser(1, "firstName", "lastName", "test@test.com", "89214445566");
        UserEntity newUser = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenReturn(newUser);
        mvc.perform(MockMvcRequestBuilders
                        .patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expected)))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    User actual = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), User.class);
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
                });
    }

    @Test
    @WithAnonymousUser
    void updateUserUnauthorized() throws Exception {
        User expected = createUserDto(1, "testFirstName", "testLastName", "test@test.com", "89211234578");

        mvc.perform(MockMvcRequestBuilders
                        .patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expected)))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                });
    }


    @Test
    @WithMockUser(password = "oldPassword")
    void setPassword() throws Exception {
        NewPassword expected = createPassword("oldPassword", "newPassword");
        NewPassword newPassword = createPassword("oldPassword", "newPassword");
        UserEntity user = createUser(1, "firstName", "lastName", "test@test.com", "89214445566");
        user.setPassword(passwordEncoder.encode("oldPassword"));
        when(userRepository.findUserEntityByEmail(any(String.class)))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        mvc.perform(MockMvcRequestBuilders
                        .post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPassword)))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    NewPassword actual = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), NewPassword.class);
                    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
                });
        mvc.perform(MockMvcRequestBuilders
                        .post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPassword)))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
                });
    }

    @Test
    @WithMockUser(password = "badPassword")
    void setPasswordWrongPassword() throws Exception {
        NewPassword expected = createPassword("badPassword", "newPassword");
        NewPassword newPassword = createPassword("badPassword", "newPassword");
        UserEntity user = createUser(1, "firstName", "lastName", "test@test.com", "89214445566");
        user.setPassword(passwordEncoder.encode("oldPassword"));
        when(userRepository.findUserEntityByEmail(any(String.class)))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        mvc.perform(MockMvcRequestBuilders
                        .post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPassword)))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
                });
    }

    @Test
    @WithMockUser
    void updateUserImage() throws Exception {
        Path path = Paths.get(AvatarServiceImpl.class.getResource("test.gif").toURI());
        String imageNewPath = path.getParent().toString();
        MockMultipartFile multipartFile = new MockMultipartFile("image",
                "test.gif", "image/gif", Files.readAllBytes(path));
        UserEntity user = createUser(1, "firstName", "lastName", "test@test.com", "89214445566");
        UserAvatar avatar = createAvatar(multipartFile, 1);

        ReflectionTestUtils.setField(avatarService, "avatarFolder", imageNewPath);

        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(avatarRepository.save(any(UserAvatar.class))).thenReturn(avatar);
        when(userRepository.findUserEntityByEmail(any(String.class)))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                        .multipart("/users/me/image")
                        .file(multipartFile)
                        .with(new RequestPostProcessor() {
                            @Override
                            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                                request.setMethod("PATCH");
                                return request;
                            }
                        }))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                });
        File actualFile = new File(imageNewPath + "/" + avatar.getId() + ".gif");
        File expectedFile = new File(imageNewPath + "/test.gif");
        AssertionsForClassTypes.assertThat(actualFile).hasSameBinaryContentAs(expectedFile);
        actualFile.delete();

        mvc.perform(MockMvcRequestBuilders
                        .multipart("/users/me/image")
                        .file(multipartFile)
                        .with(new RequestPostProcessor() {
                            @Override
                            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                                request.setMethod("PATCH");
                                return request;
                            }
                        }))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
                });
    }

    @Test
    @WithMockUser("test")
    void loadImage() throws Exception {

        Path path = Paths.get(AvatarServiceImpl.class.getResource("11.gif").toURI());

        MultipartFile multipartFile = new MockMultipartFile("image",
                "test.gif", "image/gif", Files.readAllBytes(path));

        UserAvatar avatar = createAvatar(multipartFile, 11);
        avatar.setFilePath(path.toString());

        Pair<String, byte[]> expected = Pair.of(avatar.getMediaType(), multipartFile.getBytes());

        when(avatarRepository.findById(any(Integer.class)))
                .thenReturn(Optional.of(avatar));

        mvc.perform(MockMvcRequestBuilders
                        .get("/users/me/image/11"))
                .andExpect(result -> {
                    MockHttpServletResponse mockHttpServletResponse = result.getResponse();
                    assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(mockHttpServletResponse.getContentAsByteArray()).isEqualTo(expected.getSecond());
                    assertThat(mockHttpServletResponse.getContentType()).isEqualTo(expected.getFirst());
                });

    }

    private User createUserDto(Integer id, String firstName, String lastName, String email, String phone) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        return user;
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

    private NewPassword createPassword(String oldPassword, String newPassword) {
        NewPassword password = new NewPassword();
        password.setCurrentPassword(oldPassword);
        password.setNewPassword(newPassword);
        return password;
    }

    private UserAvatar createAvatar(MultipartFile avatarFile, Integer id) throws IOException {
        UserAvatar avatar = new UserAvatar();
        avatar.setId(id);
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setFileSize(avatarFile.getSize());
        return avatar;
    }
}
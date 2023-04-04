package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.exception.UserNotRegisterException;
import ru.skypro.homework.model.UserAvatar;
import ru.skypro.homework.service.AvatarService;
import ru.skypro.homework.service.UserService;
import ru.skypro.homework.service.impl.AvatarServiceImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AvatarService avatarService;

    @Test
    @WithMockUser("test@test.com")
    void getUsers() throws Exception {
        User expected = createUserDto(1, "newFirstName", "newLastName", "test@test.com", "89211231212");
        when(userService.getUsers(any(Authentication.class))).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse mockHttpServletResponse = result.getResponse();
                    User actual = objectMapper.readValue(mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8), User.class);
                    assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
                });
    }

    @Test
    @WithAnonymousUser
    void getUsersUnauthorized() throws Exception {
        User expected = createUserDto(1, "newFirstName", "newLastName", "test@test.com", "89211231212");
        when(userService.getUsers(any(Authentication.class))).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/me")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("test@test.com")
    void getUsersNotRegister() throws Exception {
        when(userService.getUsers(any(Authentication.class))).thenThrow(UserNotRegisterException.class);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/me")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void updateUserUnauthorized() throws Exception {
        User expected = createUserDto(1, "newFirstName", "newLastName", "test@test.com", "89211231212");
        User patchUser = createUserDto(1, "newFirstName", "newLastName", "test@test.com", "89211231212");

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/users/me")
                .with(SecurityMockMvcRequestPostProcessors.csrf())  // Зачем???? Но когда убираю, то вместо 401 идет 403
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchUser))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("test")
    void updateUserForbidden() throws Exception {
        User expected = createUserDto(1, "newFirstName", "newLastName", "test@test.com", "89211231212");
        User patchUser = createUserDto(1, "newFirstName", "newLastName", "test@test.com", "89211231212");

        when(userService.updateUser(any(User.class), any(Authentication.class))).thenThrow(UserNotRegisterException.class);
        mockMvc.perform(MockMvcRequestBuilders
                .patch("/users/me")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchUser))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser("test")
    void updateUser() throws Exception {
        User expected = createUserDto(1, "newFirstName", "newLastName", "test@test.com", "89211231212");
        User patchUser = createUserDto(1, "newFirstName", "newLastName", "test@test.com", "89211231212");

        when(userService.updateUser(any(User.class), any(Authentication.class))).thenReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/me")
                        .content(objectMapper.writeValueAsString(patchUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(result -> {
                    MockHttpServletResponse mockHttpServletResponse = result.getResponse();
                    User actual = objectMapper.readValue(mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8), User.class);
                    assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
                });
    }

    @Test
    @WithAnonymousUser
    void setPasswordUnauthorized() throws Exception {
        NewPassword password = createPassword("1", "2");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/set_password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())  // Зачем???? Но когда убираю, то вместо 401 идет 403
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(password))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("test")
    void setPasswordForbidden() throws Exception {
        NewPassword password = createPassword("1", "2");

        when(userService.setPassword(any(NewPassword.class), any(Authentication.class))).thenThrow(UserNotRegisterException.class);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/set_password")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(password))
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser("test")
    void setPassword() throws Exception {
        NewPassword expected = createPassword("1", "2");
        NewPassword newPassword = createPassword("1", "2");
        when(userService.setPassword(any(NewPassword.class), any(Authentication.class))).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders.post("/users/set_password")
                        .content(objectMapper.writeValueAsString(newPassword))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(result -> {
                    MockHttpServletResponse mockHttpServletResponse = result.getResponse();
                    NewPassword actual = objectMapper.readValue(mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8), NewPassword.class);
                    assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
                });
    }

    @Test
    @WithAnonymousUser
    void updateUserImageUnauthorized() throws Exception {
        Path path = Paths.get(AvatarServiceImpl.class.getResource("test.gif").toURI());
        MockMultipartFile multipartFile = new MockMultipartFile("avatarFile",
                "test.gif", "image/gif", Files.readAllBytes(path));

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/users/me/image")
                        .file(multipartFile)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // Зачем???? Но когда убираю, то вместо 401 идет 403
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("test")
    void updateUserImageForbidden() throws Exception {
        Path path = Paths.get(AvatarServiceImpl.class.getResource("test.gif").toURI());
        MockMultipartFile multipartFile = new MockMultipartFile("image",
                "test.gif", "image/gif", Files.readAllBytes(path));

        doThrow(UserNotRegisterException.class).when(avatarService).uploadAvatar(any(MultipartFile.class), any(Authentication.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/users/me/image")
                        .file(multipartFile)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(new RequestPostProcessor() {
                            @Override
                            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                                request.setMethod("PATCH");
                                return request;
                            }
                        }))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser("test")
    void updateUserImage() throws Exception {
        Path path = Paths.get(AvatarServiceImpl.class.getResource("test.gif").toURI());
        MockMultipartFile multipartFile = new MockMultipartFile("image",
                "test.gif", "image/gif", Files.readAllBytes(path));

        doNothing().when(avatarService).uploadAvatar(any(MultipartFile.class), any(Authentication.class));
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/users/me/image")
                        .file(multipartFile)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(new RequestPostProcessor() {
                            @Override
                            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                                request.setMethod("PATCH");
                                return request;
                            }
                        }))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("test")
    void loadImage() throws Exception {

        Path path = Paths.get(AvatarServiceImpl.class.getResource("test.gif").toURI());
        MultipartFile multipartFile = new MockMultipartFile("image",
                "test.gif", "image/gif", Files.readAllBytes(path));
        UserAvatar avatar = createAvatar(multipartFile, 11);
        avatar.setFilePath(path.toString());
        Pair<String, byte[]> expected = Pair.of(avatar.getMediaType(), multipartFile.getBytes());

        when(avatarService.readAvatar(any(Integer.class))).thenReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/me/image/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
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
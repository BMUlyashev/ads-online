package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.exception.UserNotRegisterException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.AdsEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    Authentication authentication;

    @InjectMocks
    UserServiceImpl userService;

    @Spy
    UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void getUsers() {
        UserEntity user = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        User expected = createUserDto(1, "testFirstName", "testLastName", "test@test.com", "89211234578");

        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));

        User actual = userService.getUsers(authentication);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getUsersThrowException() {
        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUsers(authentication)).isInstanceOf(UserNotRegisterException.class);
    }

    @Test
    void updateUser() {
        User expected = createUserDto(1, "newFirstName", "newLastName", "test@test.com", "89211231212");
        UserEntity newUser = createUser(1, "newFirstName", "newLastName", "test@test.com", "89211231212");
        UserEntity user = createUser(1, "firstName", "lastName", "test@test.com", "89212221133");

        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenReturn(newUser);

        User actual = userService.updateUser(expected, authentication);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateUserThrowException() {
        User user = createUserDto(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUser(user, authentication)).isInstanceOf(UserNotRegisterException.class);
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

    private User createUserDto(Integer id, String firstName, String lastName, String email, String phone) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        return user;
    }
}
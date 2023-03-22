package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    AuthServiceImpl authService;

    @Mock
    UserRepository userRepository;

    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Spy
    UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void loginReturnFalseUserNotFound() {
        UserEntity user = createUser(1, "test", "test", "test@mail.com", "1", Role.USER,
                "$2a$12$bwUFD4eVKNv.QLSPv/jYKe0vWAkAKWGMKejRQC3FN4SufBADHuJnS");

        when(userRepository.findUserEntityByEmail(user.getEmail())).thenReturn(Optional.empty());
        assertThat(authService.login("test@mail.com", "test")).isFalse();

    }

    @Test
    void loginReturnTrueIfPasswordCorrect() {
        UserEntity user = createUser(1, "test", "test", "test@mail.com", "1", Role.USER,
                "$2a$12$bwUFD4eVKNv.QLSPv/jYKe0vWAkAKWGMKejRQC3FN4SufBADHuJnS");

        when(userRepository.findUserEntityByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertThat(authService.login("test@mail.com", "test")).isTrue();
    }

    @Test
    void loginReturnFalseIfPasswordIncorrect() {
        UserEntity user = createUser(1, "test", "test", "test@mail.com", "1", Role.USER,
                "$2a$12$bwUFD4eVKNv.QLSPv/jYKe0vWAkAKWGMKejRQC3FN4SufBADHuJnS");

        when(userRepository.findUserEntityByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertThat(authService.login("test@mail.com", "wrong")).isFalse();
    }

    @Test
    void registerReturnFalseUserAlreadyRegister() {
        UserEntity user = createUser(1, "test", "test", "test@mail.com", "1", Role.USER,
                "$2a$12$bwUFD4eVKNv.QLSPv/jYKe0vWAkAKWGMKejRQC3FN4SufBADHuJnS");
        RegisterReq reg = createRegister("test@mail.com", "password",
                "xxx", "xxx", "1", Role.USER);
        when(userRepository.findUserEntityByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertThat(authService.register(reg, Role.USER)).isFalse();
    }

    @Test
    void registerReturnTrueUserRegister() {
        RegisterReq reg = createRegister("test@mail.com", "password",
                "xxx", "xxx", "1", Role.USER);
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.empty());
        assertThat(authService.register(reg, Role.USER)).isTrue();
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }


    private UserEntity createUser(Integer id, String firstName, String lastName, String email, String phone, Role role, String password) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        user.setPassword(password);
        return user;
    }

    private RegisterReq createRegister(String username, String password, String firstName, String lastName, String phone, Role role) {
        RegisterReq reg = new RegisterReq();
        reg.setUsername(username);
        reg.setPassword(password);
        reg.setFirstName(firstName);
        reg.setLastName(lastName);
        reg.setPhone(phone);
        reg.setRole(role);
        return reg;
    }
}
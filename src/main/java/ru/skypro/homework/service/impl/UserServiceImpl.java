package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.exception.IncorrectPasswordException;
import ru.skypro.homework.exception.UserForbiddenException;
import ru.skypro.homework.exception.UserNotRegisterException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Реализация интерфейса {@link UserService}
 */
@Log
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User getUsers(Authentication authentication) {
        log.info("completed getUsers");
        UserEntity userEntity = userRepository.findUserEntityByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotRegisterException(authentication.getName()));
        return userMapper.toDTO(userEntity);
    }

    @Override
    public User updateUser(User user, Authentication authentication) {
        log.info("completed updateUser");
        UserEntity userEntity = userRepository.findUserEntityByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotRegisterException(authentication.getName()));
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setPhone(user.getPhone());
        return userMapper.toDTO(userRepository.save(userEntity));
    }

    @Override
    public NewPassword setPassword(NewPassword newPassword, Authentication authentication) {
        Pattern pattern = Pattern.compile("\\w{8,}");
        Matcher matcher = pattern.matcher(newPassword.getNewPassword());
        if (!matcher.matches()) {
            throw new IncorrectPasswordException("Неверный формат пароля");
        }
        log.info("completed setPassword");
        UserEntity userEntity = userRepository.findUserEntityByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotRegisterException(authentication.getName()));
        if (passwordEncoder.matches(newPassword.getCurrentPassword(), userEntity.getPassword())) {
            userEntity.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
            userRepository.save(userEntity);
            return newPassword;
        }
        throw new UserForbiddenException(userEntity.getId());
    }
}

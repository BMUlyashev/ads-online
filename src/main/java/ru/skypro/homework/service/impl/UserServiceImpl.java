package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.exception.UserNotRegisterException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;


    @Override
    public User getUsers(Authentication authentication) {
        UserEntity userEntity = userRepository.findUserEntityByEmail(authentication.getName())
                        .orElseThrow(() -> new UserNotRegisterException(authentication.getName()));
        return userMapper.toDTO(userEntity);
    }

    @Override
    public User updateUser(User user) {
        UserEntity userEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException(user.getId()));
        userEntity.setEmail(user.getEmail());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setPhone(user.getPhone());
        userEntity.setRegDate(LocalDateTime.parse(user.getRegDate()));
        userRepository.save(userEntity);
        return userMapper.toDTO(userEntity);
    }

    @Override
    public NewPassword setPassword(NewPassword newPassword) {
        return null;
    }

    @Override
    public byte[] updateUserImage(MultipartFile image) {
        return new byte[0];
    }
}
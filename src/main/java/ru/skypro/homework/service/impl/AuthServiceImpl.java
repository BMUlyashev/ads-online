package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.exception.IncorrectPasswordException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Реализация интерфейса {@link AuthService}
 */
@Log
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public boolean login(String userName, String password) {
        log.info("completed login");
        UserEntity user = userRepository.findUserEntityByEmail(userName).orElse(null);
        if (user == null) {
            return false;
        }

        String encryptedPassword = user.getPassword();
        return passwordEncoder.matches(password, encryptedPassword);
    }

    @Override
    public boolean register(RegisterReq regReq, Role role) {
        Pattern pattern = Pattern.compile("\\w{8,}");
        Matcher matcher = pattern.matcher(regReq.getPassword());
        if (!matcher.matches()) {
            log.info("incorrect password format");
            throw new IncorrectPasswordException("Неверный формат пароля");
        }
        log.info("completed register");

        if (userRepository.findUserEntityByEmail(regReq.getUsername()).isPresent()) {
            return false;
        }
        UserEntity user = userMapper.fromRegisterReq(regReq);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }
}

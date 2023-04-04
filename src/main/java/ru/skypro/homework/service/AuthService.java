package ru.skypro.homework.service;

import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
/**
 * Интерфейс для работы с логированием и регистрацией пользователя
 */
public interface AuthService {
    /**
     * Авторизация пользователя
     * @param userName - имя пользователя;
     * @param password - пароль пользователя;
     * @return результат авторизации
     */
    boolean login(String userName, String password);

    /**
     * Регистрация пользователя
     * @param registerReq - регистрационные данные;
     * @param role - роль;
     * @return результат регистрации
     */
    boolean register(RegisterReq registerReq, Role role);
}

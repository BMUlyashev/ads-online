package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.model.UserEntity;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "regDate", source = "regDate", dateFormat = "dd-MM-yyyy HH:mm:ss")
    UserEntity toEntity(User user);

    @Mapping(target = "regDate", source = "regDate", dateFormat = "dd-MM-yyyy HH:mm:ss")
    User toDTO(UserEntity userEntity);

    Collection<UserEntity> toEntityList(Collection<User> user);

    Collection<User> toUserList(Collection<UserEntity> userEntities);

    @Mapping(target = "email", source = "username")
    UserEntity fromRegisterReq(RegisterReq registerReq);
}

package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.SecurityUserDto;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.model.UserAvatar;
import ru.skypro.homework.model.UserEntity;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface UserMapper {


    UserEntity toEntity(User user);

    @Mapping(target = "image", source = "avatar", qualifiedByName = "imagePathToUrl")
    User toDTO(UserEntity userEntity);

    @Named("imagePathToUrl")
    static String imagePathToUrl(UserAvatar avatar) {
        if (avatar == null) {
            return null;
        }
        return "/users/me/image/" + avatar.getId();
    }

    Collection<UserEntity> toEntityList(Collection<User> user);

    Collection<User> toUserList(Collection<UserEntity> userEntities);

    @Mapping(target = "email", source = "username")
    UserEntity fromRegisterReq(RegisterReq registerReq);

    SecurityUserDto toSecurityUserDto(UserEntity user);
}

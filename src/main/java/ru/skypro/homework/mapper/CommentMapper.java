package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.model.UserEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", source = "pk")
    @Mapping(target = "user.id", source = "author")
    @Mapping(target = "user.firstName", source = "authorFirstName")
    @Mapping(target = "createdAt", ignore = true)
    CommentEntity dtoToModel(Comment comment);

    @Mapping(target = "pk", source = "id")
    @Mapping(target = "author", source = "user.id")
    @Mapping(target = "authorFirstName", source = "user.firstName")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateToInstant")
    @Mapping(target = "authorImage", source = "user", qualifiedByName = "userAvatar")
    Comment modelToDto(CommentEntity commentEntity);

    List<CommentEntity> dtoToModel(List<Comment> comments);

    List<Comment> modelToDto(List<CommentEntity> comments);

    @Named("localDateToInstant")
    static Instant localDateToInstant(LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).toInstant();
    }

    @Named("userAvatar")
    static String userAvatar(UserEntity user) {
        if (user.getAvatar() == null) {
            return null;
        }
        return "/users/me/image/" + user.getAvatar().getId();
    }
}

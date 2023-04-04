package ru.skypro.homework.model;

import lombok.Data;
import ru.skypro.homework.dto.Role;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

/**
 * Сущность пользователя
 */
@Entity
@Data
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    @Enumerated(EnumType.STRING)
    private Role role;

    private String password;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<AdsEntity> adsList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Collection<CommentEntity> commentEntities;

    @OneToOne
    private UserAvatar avatar;
}

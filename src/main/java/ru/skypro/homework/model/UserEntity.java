package ru.skypro.homework.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
    private LocalDateTime regDate;
    private boolean adminRole;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<AdsEntity> adsList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Collection<CommentEntity> commentEntities;
}

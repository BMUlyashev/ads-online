package ru.skypro.homework.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
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

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<AdsEntity> adsList;

  @OneToMany(mappedBy = "user")
  private Collection<CommentEntity> commentEntities;
}

package ru.skypro.homework.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "avatars")
public class UserAvatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String filePath;
    private long fileSize;
    private String mediaType;
}

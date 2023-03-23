package ru.skypro.homework.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class Comment {
    private Integer author;
    private Instant createdAt;
    private Integer pk;
    private String text;
    private String authorImage;
    private String authorFirstName;
}

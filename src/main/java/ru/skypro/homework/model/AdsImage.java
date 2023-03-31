package ru.skypro.homework.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "ads_images")
public class AdsImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "path")
    private String path;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "media_type")
    private String mediaType;


    public AdsImage(){
    }

    public AdsImage(Integer id, String path, Long fileSize, String mediaType) {
        this.id = id;
        this.path = path;
        this.fileSize = fileSize;
        this.mediaType = mediaType;
    }
}

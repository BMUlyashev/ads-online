package ru.skypro.homework.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "ads_images")
public class AdsImage {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "path")
    private String path;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "media_type")
    private String mediaType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ads_id")
    private AdsEntity adsEntity;
}

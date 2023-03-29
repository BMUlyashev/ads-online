package ru.skypro.homework.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;


@Entity
@Data
@Table(name = "ads")
public class AdsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;

    @OneToMany(mappedBy = "ads", cascade = CascadeType.ALL)
    private Collection<CommentEntity> commentEntities;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private AdsImage image;

    public AdsEntity(){

    }
    public AdsEntity(Integer id, String title, String description, Integer price){
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
    }
}

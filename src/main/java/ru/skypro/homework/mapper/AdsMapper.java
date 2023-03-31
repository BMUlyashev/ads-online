package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateAds;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.model.AdsEntity;
import ru.skypro.homework.model.AdsImage;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdsMapper {

    @Mapping(source = "title", target = "title")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "description", target = "description")
    AdsEntity createAdsToAdsEntity(CreateAds ads);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
   @Mapping(source = "image", target = "image", qualifiedByName = "imageToString") // for image
    Ads adsEntityToAds(AdsEntity adsEntity);

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.lastName", target = "authorLastName")
    @Mapping(source = "author.email", target = "email")
    @Mapping(source = "author.phone", target = "phone")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "image", target = "image", qualifiedByName = "imageToString")
    FullAds adsEntityToFullAds(AdsEntity adsEntity);

    @Named("imageToString")
    static String imageToString(AdsImage adsImage){
        if (adsImage == null) {
            return null;
        }
        return "/ads/me/image/" + adsImage.getId();
    }
    List<Ads> adsEntityToAdsList(List<AdsEntity> adsEntities);

}

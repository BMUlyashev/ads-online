package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateAds;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.dto.ResponseWrapperAds;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.UserNotRegisterException;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.model.AdsEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdsService;

@Service
@RequiredArgsConstructor
public class AdsServiceImpl implements AdsService {

    private final AdsMapper adsMapper;

    private final AdsRepository adsRepository;

    private final UserRepository userRepository;
    @Override
    public ru.skypro.homework.dto.Ads addAds(CreateAds properties, MultipartFile image, Authentication authentication) {
        // mapping from dto to entity
        UserEntity author = userRepository.findUserEntityByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotRegisterException(authentication.getName()));
        AdsEntity adsEntity = adsMapper.createAdsToAdsEntity(properties);
        adsEntity.setAuthor(author);
        //TODO image
        return adsMapper.adsEntityToAds(adsRepository.save(adsEntity));
    }

    @Override
    public void deleteAds(Integer id) {
        AdsEntity adsEntity = adsRepository.findById(id).orElseThrow(() -> new AdsNotFoundException(id));
        adsRepository.deleteById(id);
    }

    @Override
    public Ads updateAds(Integer id, CreateAds createAds, Authentication authentication) {
        AdsEntity adsEntity = adsRepository.findById(id).orElseThrow(() -> new AdsNotFoundException(id));
        adsEntity.setDescription(createAds.getDescription());
        adsEntity.setPrice(createAds.getPrice());
        adsEntity.setTitle(createAds.getTitle());
        return adsMapper.adsEntityToAds(adsRepository.save(adsEntity));
    }

    @Override
    public FullAds getFullAds(Integer id) {
        return adsMapper.adsEntityToFullAds(adsRepository.findById(id).orElseThrow(() -> new AdsNotFoundException(id)));
    }

    @Override
    public ResponseWrapperAds getAllAds() {
        ResponseWrapperAds responseWrapperAds = new ResponseWrapperAds();
        responseWrapperAds.setResults(adsMapper.adsEntityToAdsList(adsRepository.findAll()));
        int countAds = responseWrapperAds.getResults().size();
        responseWrapperAds.setCount(countAds);
        //entity to ads
        return responseWrapperAds;
    }

    @Override
    public ResponseWrapperAds getAdsMe(Authentication authentication) {
        UserEntity author = userRepository.findUserEntityByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotRegisterException(authentication.getName()));
        ResponseWrapperAds responseWrapperAds = new ResponseWrapperAds();
        responseWrapperAds.setResults(adsMapper.adsEntityToAdsList(adsRepository.findAllByAuthor_Id(author.getId())));
        responseWrapperAds.setCount(responseWrapperAds.getResults().size());
        return responseWrapperAds;
    }

    @Override
    public ResponseWrapperAds getAllAdsFilter(String filter) {
        ResponseWrapperAds responseWrapperAds = new ResponseWrapperAds();
        responseWrapperAds.setResults(adsMapper.adsEntityToAdsList(adsRepository.findByTitleContainingIgnoreCase(filter)));
        int countAds = responseWrapperAds.getResults().size();
        responseWrapperAds.setCount(countAds);
        return responseWrapperAds;
    }
}

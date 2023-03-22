package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.ResponseWrapperComment;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.UserForbiddenException;
import ru.skypro.homework.exception.UserNotRegisterException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CommentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AdsRepository adsRepository;
    private final UserRepository userRepository;
    private final UserValidatePermission validatePermission;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


    @Override
    public Comment addComment(Integer id, Comment comment, Authentication authentication) {

        CommentEntity newComment = commentMapper.dtoToModel(comment);

        newComment.setUser(userRepository.findUserEntityByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotRegisterException(authentication.getName())));
        newComment.setAds(adsRepository.findById(id)
                .orElseThrow(() -> new AdsNotFoundException(id)));
        newComment.setCreatedAt(LocalDateTime.now());

        return commentMapper.modelToDto(commentRepository.save(newComment));
    }

    @Override
    public Comment updateComment(Integer adId, Integer commentId, Comment comment, Authentication authentication) {

        CommentEntity findComment = findComment(adId, commentId);

        UserEntity findUser = userRepository.findUserEntityByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotRegisterException(authentication.getName()));

        if(validatePermission.isAdmin(findUser) || validatePermission.isCommentOwner(findUser, findComment)) {
            findComment.setText(comment.getText());
            findComment.setCreatedAt(LocalDateTime.now());
            return commentMapper.modelToDto(commentRepository.save(findComment));
        } else {
            throw new UserForbiddenException(findUser.getId());
        }
    }

    @Override
    public Comment getComment(Integer adId, Integer commentId) {
        return commentMapper.modelToDto(findComment(adId, commentId));
    }

    @Override
    public void deleteComment(Integer adId, Integer commentId, Authentication authentication) {
        CommentEntity comment = findComment(adId, commentId);
        UserEntity user = userRepository.findUserEntityByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + authentication.getName()));
        if (validatePermission.isAdmin(user) || validatePermission.isCommentOwner(user, comment)) {
            commentRepository.deleteById(commentId);
            return;
        }
        throw new UserForbiddenException(user.getId());
    }

    @Override
    public ResponseWrapperComment getAllCommentsByAd(Integer id) {
        List<CommentEntity> comments = commentRepository.findAllByAds_Id(id);
        ResponseWrapperComment findComments = new ResponseWrapperComment();
        findComments.setResults(commentMapper.modelToDto(comments));
        findComments.setCount(comments.size());
        return findComments;
    }

    private CommentEntity findComment(Integer adId, Integer commentId) {
        return commentRepository.findByAds_IdAndId(adId, commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }
}

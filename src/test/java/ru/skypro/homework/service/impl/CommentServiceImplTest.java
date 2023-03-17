package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.ResponseWrapperComment;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.UserForbiddenException;
import ru.skypro.homework.exception.UserNotRegisterException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.model.AdsEntity;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    CommentServiceImpl out;
    @Mock
    CommentRepository commentRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CommentMapper commentMapper;
    @Mock
    AdsRepository adsRepository;
    @Mock
    Authentication authentication;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private CommentEntity commentEntity1;
    private CommentEntity commentEntity2;
    private AdsEntity adsEntity1;
    private AdsEntity adsEntity2;

    private Comment comment1;
    private Comment comment2;
    private UserEntity user;


    @BeforeEach
    public void setOut() {
        user = new UserEntity();
        user.setId(1);
        user.setEmail("test@test.com");


        comment1 = new Comment();
        comment1.setPk(1);
        comment1.setText("test1");
        comment1.setAuthor(1);
        comment1.setCreatedAt("05-01-2021 15:33:25");

        comment2 = new Comment();
        comment2.setPk(2);
        comment2.setText("test2");
        comment2.setAuthor(1);
        comment2.setCreatedAt("05-01-2021 15:35:25");

        commentEntity1 = new CommentEntity();
        commentEntity1.setId(1);
        commentEntity1.setText("test1");
        commentEntity1.setUser(user);
        commentEntity1.setAds(adsEntity1);
        commentEntity1.setCreatedAt(LocalDateTime.parse("05-01-2021 15:33:25", dateTimeFormatter));

        commentEntity2 = new CommentEntity();
        commentEntity2.setId(2);
        commentEntity2.setText("test2");
        commentEntity2.setUser(user);
        commentEntity2.setAds(adsEntity2);
        commentEntity2.setCreatedAt(LocalDateTime.parse("05-01-2021 15:35:25", dateTimeFormatter));

        adsEntity1 = new AdsEntity();
        adsEntity1.setId(1);
        adsEntity2 = new AdsEntity();
        adsEntity2.setId(2);

    }

    @Test
    void addComment() {
        Integer id1 = 1;
        Integer id2 = 2;
        String email = "test@test.com";
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findUserEntityByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(commentMapper.dtoToModel(comment1)).thenReturn(commentEntity1);
        when(commentMapper.dtoToModel(comment2)).thenReturn(commentEntity2);
        when(commentRepository.save(commentEntity1)).thenReturn(commentEntity1);
        when(commentRepository.save(commentEntity2)).thenReturn(commentEntity2);
        when(adsRepository.findById(id1)).thenReturn(Optional.ofNullable(adsEntity1));
        when(adsRepository.findById(id2)).thenReturn(Optional.ofNullable(adsEntity2));
        when(commentMapper.modelToDto(commentEntity1)).thenReturn(comment1);
        when(commentMapper.modelToDto(commentEntity2)).thenReturn(comment2);

        Comment expected1 = comment1;
        Comment expected2 = comment2;

        Comment actual1 = out.addComment(id1, comment1, authentication);
        Comment actual2 = out.addComment(id2, comment2, authentication);

        assertThat(actual1).isEqualTo(expected1);
        assertThat(actual2).isEqualTo(expected2);


    }

    @Test
    void addCommentAdsNotFound(){
        Integer id1 = 1;
        String email = "test@test.com";
        when(commentMapper.dtoToModel(comment1)).thenReturn(commentEntity1);
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findUserEntityByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(adsRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> out.addComment(id1, comment1, authentication)).isInstanceOf(AdsNotFoundException.class);
    }

    @Test
    void addCommentUserNotRegister(){
        Integer id1 = 1;
        String email = "test@test.com";
        when(commentMapper.dtoToModel(comment1)).thenReturn(commentEntity1);
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findUserEntityByEmail(email)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> out.addComment(id1, comment1, authentication)).isInstanceOf(UserNotRegisterException.class);
    }

    @Test
    void updateComment() {
        CommentEntity commentEntity3 = new CommentEntity();
        commentEntity3.setId(1);
        commentEntity3.setText("test2");
        commentEntity3.setUser(user);
        commentEntity3.setAds(adsEntity1);
        commentEntity3.setCreatedAt(LocalDateTime.parse("05-01-2021 15:35:25", dateTimeFormatter));

        Comment comment3 = new Comment();
        comment3.setPk(1);
        comment3.setText("test2");
        comment3.setAuthor(1);
        comment3.setCreatedAt("05-01-2021 15:35:25");

        String email = "test@test.com";
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findUserEntityByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(commentRepository.findByAds_IdAndId(1, 1)).thenReturn(Optional.ofNullable(commentEntity1));
        when(commentRepository.save(any())).thenReturn(commentEntity3);
        when(commentMapper.modelToDto((CommentEntity)any())).thenReturn(comment3);

        Comment expected = comment3;
        Comment actual = out.updateComment(1, 1, comment3, authentication);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateCommentNotFound() {
        when(commentRepository.findByAds_IdAndId(1, 1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> out.updateComment(1, 1, comment1, authentication))
                .isInstanceOf(CommentNotFoundException.class);

    }

    @Test
    void updateCommentUserNotForbidden() {
        String email = "test@test.com";
        UserEntity user2 = new UserEntity();
        user2.setId(2);
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findUserEntityByEmail(email)).thenReturn(Optional.ofNullable(user2));
        when(commentRepository.findByAds_IdAndId(1, 1)).thenReturn(Optional.ofNullable(commentEntity1));

        assertThatThrownBy(() -> out.updateComment(1, 1, comment1, authentication))
                .isInstanceOf(UserForbiddenException.class);

    }

    @Test
    void getComment() {
        when(commentRepository.findByAds_IdAndId(1, 1)).thenReturn(Optional.ofNullable(commentEntity1));
        when(commentRepository.findByAds_IdAndId(2, 2)).thenReturn(Optional.ofNullable(commentEntity2));
        when(commentMapper.modelToDto(commentEntity1)).thenReturn(comment1);
        when(commentMapper.modelToDto(commentEntity2)).thenReturn(comment2);

        Comment expected1 = comment1;
        Comment expected2 = comment2;
        Comment actual1 = out.getComment(1, 1);
        Comment actual2 = out.getComment(2, 2);

        assertThat(actual1).isEqualTo(expected1);
        assertThat(actual2).isEqualTo(expected2);
    }

    @Test
    void getCommentNotFound() {
        when(commentRepository.findByAds_IdAndId(1, 1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> out.getComment(1, 1)).isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void deleteComment() {

        when(commentRepository.findByAds_IdAndId(1, 1)).thenReturn(Optional.ofNullable(commentEntity1));
        out.deleteComment(1, 1);
        verify(commentRepository, times(1)).deleteById(1);

    }

    @Test
    void deleteCommentCommentNotFound() {
        when(commentRepository.findByAds_IdAndId(1, 1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> out.deleteComment(1, 1)).isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void getAllCommentsByAd() {
        List<CommentEntity> commentEntityList = List.of(commentEntity1, commentEntity2);
        List<Comment> commentList = List.of(comment1, comment2);
        when(commentRepository.findAllByAds_Id(1)).thenReturn(commentEntityList);
        when(commentMapper.modelToDto(commentEntityList)).thenReturn(commentList);

        ResponseWrapperComment expected = new ResponseWrapperComment();
        expected.setCount(2);
        expected.setResults(commentList);

        ResponseWrapperComment actual = out.getAllCommentsByAd(1);

        assertThat(actual).isEqualTo(expected);
    }
}
package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.model.AdsEntity;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdsImageRepository;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdsImageService;
import ru.skypro.homework.service.impl.AdsImageServiceImpl;
import ru.skypro.homework.service.impl.AdsServiceImpl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class AdsControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdsImageRepository adsImageRepository;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AdsRepository adsRepository;
    @MockBean
    private CommentRepository commentRepository;

    @SpyBean
    private AdsImageService adsImageService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Test
    @WithMockUser
    void getAdsImage() throws Exception {
        Path path = Paths.get(AdsImageServiceImpl.class.getResource("11.gif").toURI());

        MultipartFile multipartFile = new MockMultipartFile("image",
                "test.gif", "image/gif", Files.readAllBytes(path));

        AdsImage image = createImage(multipartFile, 11);
        image.setPath(path.toString());

        Pair<String, byte[]> expected = Pair.of(image.getMediaType(), multipartFile.getBytes());

        when(adsImageRepository.findById(any(Integer.class)))
                .thenReturn(Optional.of(image))
                .thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                        .get("/ads/me/image/11"))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(response.getContentAsByteArray()).isEqualTo(expected.getSecond());
                    assertThat(response.getContentType()).isEqualTo(expected.getFirst());
                });
        mvc.perform(MockMvcRequestBuilders
                        .get("/ads/me/image/11"))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    @Test
    @WithMockUser
    void updateAdsImage() throws Exception {
        Path path = Paths.get(AdsImageServiceImpl.class.getResource("test.gif").toURI());
        String imageNewPath = path.getParent().toString();
        MockMultipartFile multipartFile = new MockMultipartFile("image",
                "test.gif", "image/gif", Files.readAllBytes(path));
        UserEntity user = createUser(1, "firstName", "lastName", "test@test.com", "89214445566");
        user.setRole(Role.USER);
        AdsImage image = createImage(multipartFile, 3);
        AdsEntity adsEntity = new AdsEntity(1, "Test", "Test2", 100);
        adsEntity.setAuthor(user);
        adsEntity.setImage(image);
        image.setPath(imageNewPath + "/3.gif");
        ReflectionTestUtils.setField(adsImageService, "imageFolder", imageNewPath);

        when(adsImageRepository.save(any(AdsImage.class))).thenReturn(image);
        when(adsRepository.findById(any(Integer.class))).thenReturn(Optional.of(adsEntity));
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(adsImageRepository.findById(any(Integer.class))).thenReturn(Optional.of(image));

        mvc.perform(MockMvcRequestBuilders
                        .multipart("/ads/3/image")
                        .file(multipartFile)
                        .with(new RequestPostProcessor() {
                            @Override
                            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                                request.setMethod("PATCH");
                                return request;
                            }
                        }))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                });
        File actualFile = new File(imageNewPath + "/" + image.getId() + ".gif");
        File expectedFile = new File(imageNewPath + "/test.gif");
        AssertionsForClassTypes.assertThat(actualFile).hasSameBinaryContentAs(expectedFile);
        actualFile.delete();
    }

    @Test
    @WithMockUser
    void addAdsMe() throws Exception {
        UserEntity user = createUser(1, "firstName", "lastName", "test@test.com", "89214445566");
        List<AdsEntity> listAdsEntity = List.of(
                createAdsEntity(1, "1", "1", 100),
                createAdsEntity(2, "2", "2", 200),
                createAdsEntity(3, "3", "3", 300),
                createAdsEntity(4, "4", "4", 400)
        );
        listAdsEntity.forEach(a -> a.setAuthor(user));

        List<Ads> adsList = List.of(
                createAds(1, 100, "1"),
                createAds(2, 200, "2"),
                createAds(3, 300, "3"),
                createAds(4, 400, "4")
        );
        adsList.forEach(a -> a.setAuthor(user.getId()));
        ResponseWrapperAds expected = new ResponseWrapperAds();
        expected.setResults(adsList);
        when(userRepository.findUserEntityByEmail(any(String.class)))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.empty());
        when(adsRepository.findAllByAuthor_Id(1)).thenReturn(listAdsEntity);

        mvc.perform(MockMvcRequestBuilders
                        .get("/ads/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    ResponseWrapperAds actual = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), ResponseWrapperAds.class);
                    assertThat(actual.getCount()).isEqualTo(4);
                    assertThat(actual.getResults()).filteredOn(a -> a.getAuthor().equals(1)).hasSize(4);
                    assertThat(actual.getResults()).isEqualTo(expected.getResults());
                });
        mvc.perform(MockMvcRequestBuilders
                        .get("/ads/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
                });
    }

    @Test
    @WithMockUser
    void updateComment() throws Exception {
        Comment expected = createComment(1, "testNewComment", 1);
        UserEntity user = createUser(1, "firstName", "lastName", "test@test.com", "89214445566");
        AdsEntity adsEntity = createAdsEntity(1, "testDescription", "testTitle", 100);
        CommentEntity commentEntity = createCommentEntity(1, "test", user, adsEntity);
        CommentEntity newCommentEntity = createCommentEntity(1, "testNewComment", user, adsEntity);
        user.setRole(Role.USER);

        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(commentRepository.findByAds_IdAndId(1, 1)).thenReturn(Optional.of(commentEntity));
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(newCommentEntity);

        mvc.perform(MockMvcRequestBuilders
                        .patch("/ads/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expected)))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    Comment actual = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), Comment.class);
                    assertThat(expected).usingRecursiveComparison().ignoringFields("authorFirstName", "createdAt").isEqualTo(actual);
                });
    }

    @Test
    @WithMockUser
    void removeAds() throws Exception {

        UserEntity user = createUser(1, "firstName", "lastName", "test@test.com", "89214445566");
        AdsEntity adsEntity = createAdsEntity(1, "testDescription", "testTitle", 100);

        Path path = Paths.get(AdsImageServiceImpl.class.getResource("test.gif").toURI());
        String imageNewPath = path.getParent().toString();
        Path fileToDelete = Paths.get(imageNewPath + "/1.gif");
        Files.copy(path, fileToDelete, StandardCopyOption.REPLACE_EXISTING);
        MockMultipartFile multipartFile = new MockMultipartFile("image",
                "test.gif", "image/gif", Files.readAllBytes(fileToDelete));
        AdsImage image = createImage(multipartFile, 1);

        user.setRole(Role.USER);
        adsEntity.setImage(image);
        adsEntity.setAuthor(user);
        image.setPath(fileToDelete.toString());

        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(adsRepository.findById(any(Integer.class))).thenReturn(Optional.of(adsEntity));
        when(adsImageRepository.findById(any(Integer.class))).thenReturn(Optional.of(image));
        mvc.perform(MockMvcRequestBuilders
                        .delete("/ads/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
                    verify(adsImageRepository).deleteById(any(Integer.class));
                    verify(adsRepository).deleteById(any(Integer.class));
                    assertThat(Files.exists(fileToDelete)).isFalse();
                });
    }

    @Test
    @WithMockUser
    void getFullAd() throws Exception {
        AdsEntity adsEntity = createAdsEntity(1, "testDescription", "testTitle", 100);
        UserEntity user = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        adsEntity.setAuthor(user);

        when(adsRepository.findById(1)).thenReturn(Optional.of(adsEntity));
        FullAds expected = createFullAds("testFirstName", "testLastName",
                "testDescription", "89211234578", "test@test.com", 1, 100, "testTitle");

        mvc.perform(MockMvcRequestBuilders
                        .get("/ads/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    FullAds actual = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), FullAds.class);
                    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
                });
    }

    @Test
    @WithMockUser
    void addComments() throws Exception {
        UserEntity user = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        AdsEntity adsEntity = createAdsEntity(2, "testDescription", "testTitle", 100);
        CommentEntity expectedCommentEntity = createCommentEntity(2, "test", user, adsEntity);
        Comment expected = createComment(2, "test", 1);

        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(adsRepository.findById(any(Integer.class))).thenReturn(Optional.of(adsEntity));
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(expectedCommentEntity);

        mvc.perform(MockMvcRequestBuilders
                        .post("/ads/2/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expected)))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    Comment actual = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), Comment.class);
                    assertThat(actual.getPk()).isEqualTo(expected.getPk());
                    assertThat(actual.getText()).isEqualTo(expected.getText());
                    assertThat(actual.getAuthorFirstName()).isEqualTo(user.getFirstName());
                });
    }

    @Test
    @WithMockUser
    void addAds() throws Exception {
        CreateAds ads = createDtoCreateAds("testDescription", "testTitle", 100);
        UserEntity user = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        AdsEntity adsEntity = createAdsEntity(1, "testDescription", "testTitle", 100);
        adsEntity.setAuthor(user);
        Path path = Paths.get(AdsServiceImpl.class.getResource("test.gif").toURI());
        String imagePath = path.getParent().toString();
        MockMultipartFile image = new MockMultipartFile("image",
                "test.gif", "image/gif", Files.readAllBytes(path));
        MockMultipartFile properties = new MockMultipartFile("properties",
                "properties", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(ads).getBytes(StandardCharsets.UTF_8));
        AdsImage adsImage = createImage(image, 1);
        adsImage.setPath(imagePath + "/1.gif");
        ReflectionTestUtils.setField(adsImageService, "imageFolder", imagePath);

        Ads expected = createAds(1, 100, "testTitle");
        expected.setAuthor(1);

        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(adsImageRepository.save(any(AdsImage.class))).thenReturn(adsImage);
        when(adsRepository.save(any(AdsEntity.class))).thenReturn(adsEntity);

        mvc.perform(MockMvcRequestBuilders
                .multipart("/ads")
                .file(image)
                .file(properties))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
                    Ads actual = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), Ads.class);
                    assertThat(actual).isEqualTo(expected);
                    assertThat(Files.exists(Paths.get(adsImage.getPath()))).isTrue();
                    Files.delete(Paths.get(adsImage.getPath()));
                });
    }

    @Test
    @WithMockUser
    void getAllAds() throws Exception {
        List<AdsEntity> adsEntityList = List.of(
                createAdsEntity(1, "1", "1", 1),
                createAdsEntity(2, "2", "2", 2),
                createAdsEntity(3, "3", "3", 3),
                createAdsEntity(4, "4", "4", 4)
        );
        List<Ads> adsList = List.of(
                createAds(1, 1, "1"),
                createAds(2, 2, "2"),
                createAds(3, 3, "3"),
                createAds(4, 4, "4")
        );
        ResponseWrapperAds expected = new ResponseWrapperAds();
        expected.setResults(adsList);
        when(adsRepository.findAll()).thenReturn(adsEntityList);

        mvc.perform(MockMvcRequestBuilders
                        .get("/ads")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    ResponseWrapperAds actual = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), ResponseWrapperAds.class);
                    assertThat(actual.getCount()).isEqualTo(4);
                    assertThat(actual.getResults()).isEqualTo(expected.getResults());
                });
    }

    @Test
    @WithMockUser
    void getAllAdsFilter() throws Exception {
        List<AdsEntity> adsEntityList = List.of(
                createAdsEntity(1, "1", "1", 1),
                createAdsEntity(2, "2", "21", 2),
                createAdsEntity(3, "3", "31", 3),
                createAdsEntity(4, "4", "41", 4)
        );
        List<Ads> adsList = List.of(
                createAds(1, 1, "1"),
                createAds(2, 2, "21"),
                createAds(3, 3, "31"),
                createAds(4, 4, "41")
        );
        ResponseWrapperAds expected = new ResponseWrapperAds();
        expected.setResults(adsList);
        when(adsRepository.findAllByFilter("1")).thenReturn(adsEntityList);

        mvc.perform(MockMvcRequestBuilders
                        .get("/ads?filter=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    ResponseWrapperAds actual = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), ResponseWrapperAds.class);
                    assertThat(actual.getCount()).isEqualTo(4);
                    assertThat(actual.getResults()).isEqualTo(expected.getResults());
                });
    }

    @Test
    @WithMockUser
    void getComments() throws Exception {
        UserEntity user = createUser(1, "testFirstName", "testLastName", "test@test.com", "89211234578");
        UserEntity user2 = createUser(2, "2", "2", "test2@test.com", "89211234579");
        AdsEntity adsEntity = createAdsEntity(2, "testDescription", "testTitle", 100);

        List<CommentEntity> comments = List.of(
                createCommentEntity(1, "comment1", user, adsEntity),
                createCommentEntity(2, "comment2", user2, adsEntity)
        );
        List<Comment> commentList = List.of(
                createComment(1, "comment1", 1),
                createComment(2, "comment2", 2)
        );

        ResponseWrapperComment expected = new ResponseWrapperComment();
        expected.setCount(2);
        expected.setResults(commentList);

        when(commentRepository.findAllByAds_Id(2)).thenReturn(comments);

        mvc.perform(MockMvcRequestBuilders
                        .get("/ads/2/comments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    ResponseWrapperComment actual = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), ResponseWrapperComment.class);
                    assertThat(actual.getCount()).isEqualTo(2);
                    assertThat(actual.getResults())
                            .usingRecursiveComparison()
                            .ignoringFields("createdAt", "authorImage", "authorFirstName")
                            .isEqualTo(expected.getResults());
                });
    }

    @Test
    @WithMockUser
    void deleteComment() throws Exception {
        UserEntity user = createUser(1, "firstName", "lastName", "test@test.com", "89214445566");
        AdsEntity adsEntity = createAdsEntity(1, "testDescription", "testTitle", 100);
        CommentEntity commentEntity = createCommentEntity(1, "test", user, adsEntity);
        user.setRole(Role.USER);
        when(commentRepository.findByAds_IdAndId(1, 1)).thenReturn(Optional.of(commentEntity));
        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));

        mvc.perform(MockMvcRequestBuilders
                        .delete("/ads/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
                    verify(commentRepository).deleteById(any(Integer.class));
                });

    }

    @Test
    @WithMockUser
    void getComment() throws Exception {
        ///{adId}/comments/{commentId}
        UserEntity user = createUser(1, "firstName", "lastName", "test@test.com", "89214445566");
        AdsEntity adsEntity = createAdsEntity(1, "testDescription", "testTitle", 100);
        CommentEntity commentEntity = createCommentEntity(1, "test", user, adsEntity);
        Comment expected = createComment(1, "test", 1);
        expected.setAuthor(1);
        expected.setAuthorFirstName("firstName");

        when(commentRepository.findByAds_IdAndId(any(Integer.class), any(Integer.class))).thenReturn(Optional.of(commentEntity));

        mvc.perform(MockMvcRequestBuilders
                        .get("/ads/1/comments/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    Comment actual = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), Comment.class);
                    assertThat(expected).usingRecursiveComparison().ignoringFields("createdAt", "authorImage").isEqualTo(actual);
                });
    }

    @Test
    @WithMockUser
    void updateAds() throws Exception {
        UserEntity user = createUser(1, "firstName", "lastName", "test@test.com", "89214445566");
        AdsEntity adsEntity = createAdsEntity(1, "testDescription", "testTitle", 100);
        user.setRole(Role.USER);
        adsEntity.setAuthor(user);
        AdsEntity newAdsEntity = createAdsEntity(1, "newDescription", "newTitle", 200);
        CreateAds createAds = createCreateAds(200, "newDescription", "newTitle");
        Ads expected = createAds(1, 200, "newTitle");

        when(userRepository.findUserEntityByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(adsRepository.findById(any(Integer.class))).thenReturn(Optional.of(adsEntity));
        when(adsRepository.save(any())).thenReturn(newAdsEntity);

        mvc.perform(MockMvcRequestBuilders
                        .patch("/ads/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAds)))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    Ads actual = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), Ads.class);
                    assertThat(expected).isEqualTo(actual);
                });

    }

    private AdsImage createImage(MultipartFile image, Integer id) {
        AdsImage adsImage = new AdsImage();
        adsImage.setMediaType(image.getContentType());
        adsImage.setFileSize(image.getSize());
        adsImage.setId(id);
        return adsImage;
    }

    private UserEntity createUser(Integer id, String firstName, String lastName, String email, String phone) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        return user;
    }

    private AdsEntity createAdsEntity(Integer id, String description, String title, Integer price) {
        AdsEntity ads = new AdsEntity();
        ads.setDescription(description);
        ads.setTitle(title);
        ads.setPrice(price);
        ads.setId(id);
        return ads;
    }

    private Ads createAds(Integer pk, Integer price, String title) {
        Ads ads = new Ads();
        ads.setPk(pk);
        ads.setPrice(price);
        ads.setTitle(title);
        return ads;
    }

    private CommentEntity createCommentEntity(int id, String commentText, UserEntity user, AdsEntity adsEntity) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setId(id);
        commentEntity.setText(commentText);
        commentEntity.setUser(user);
        commentEntity.setAds(adsEntity);
        commentEntity.setCreatedAt(LocalDateTime.parse("05-01-2021 15:35:25", dateTimeFormatter));
        return commentEntity;
    }

    private Comment createComment(int pk, String textComment, int authorID) {
        Comment comment = new Comment();
        comment.setPk(pk);
        comment.setText(textComment);
        comment.setAuthor(authorID);
        comment.setCreatedAt(Instant.ofEpochMilli(1556175797428L));
        return comment;
    }

    private CreateAds createCreateAds(int price, String description, String title) {
        CreateAds ads = new CreateAds();
        ads.setPrice(price);
        ads.setDescription(description);
        ads.setTitle(title);
        return ads;
    }

    private FullAds createFullAds(String firstName, String lastName, String description, String phone, String email, Integer pk, Integer price, String title) {
        FullAds ads = new FullAds();
        ads.setAuthorFirstName(firstName);
        ads.setAuthorLastName(lastName);
        ads.setDescription(description);
        ads.setPhone(phone);
        ads.setEmail(email);
        ads.setPk(pk);
        ads.setPrice(price);
        ads.setTitle(title);
        return ads;
    }

    private CreateAds createDtoCreateAds(String description, String title, Integer price) {
        CreateAds ads = new CreateAds();
        ads.setDescription(description);
        ads.setTitle(title);
        ads.setPrice(price);
        return ads;
    }
}
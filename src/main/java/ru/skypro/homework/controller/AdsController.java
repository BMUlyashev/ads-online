package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.CommentService;


@CrossOrigin(value = "http://localhost:3000")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/ads")
public class AdsController {

    private final AdsService adsService;
    private final CommentService commentService;

    @Operation(summary = "getAllAds", description = "Запрос списка всех объявлений",
            tags = {"Объявления"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseWrapperAds.class)))
            })
    @GetMapping
    public ResponseEntity<ResponseWrapperAds> getAllAds() {
        return ResponseEntity.ok(adsService.getAllAds());
    }

    @Operation(summary = "addAds", description = "Добавление нового объявление",
            tags = {"Объявления"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новое объявление",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ads> addAds(@RequestPart CreateAds properties,
                                      @RequestPart MultipartFile image,
                                      Authentication authentication) {
        System.out.println(properties.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(adsService.addAds(properties, image, authentication));
    }

    @Operation(summary = "getComments", description = "Запрос списка всех комментариев к объявлению",
            tags = {"Объявления"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseWrapperComment.class))),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @GetMapping("/{id}/comments")
    public ResponseEntity<ResponseWrapperComment> getComments(@PathVariable Integer id) {
        return ResponseEntity.ok(commentService.getAllCommentsByAd(id));
    }

    @Operation(summary = "addComments", description = "Добавление комментария пользователя",
            tags = {"Объявления"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новый комментарий",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Comment.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Comment.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> addComments(@PathVariable Integer id,
                                               @RequestBody Comment comment,
                                               Authentication authentication) {
        return ResponseEntity.ok(commentService.addComment(id, comment, authentication));
    }

    @Operation(summary = "getFullAd", description = "Запрос полной информации по объявлению",
            tags = {"Объявления"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = FullAds.class))),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @GetMapping("/{id}")
    public ResponseEntity<FullAds> getFullAd(@PathVariable Integer id, Authentication authentication) {
        return ResponseEntity.ok(adsService.getFullAds(id));
    }

    @Operation(summary = "removeAds", description = "Удаление объявления",
            tags = {"Объявления"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity removeAds(@PathVariable Integer id) {
        adsService.deleteAds(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "updateAds", description = "Обновление объявления",
            tags = {"Объявления"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленные данные объявления",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateAds.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Ads.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @PatchMapping("/{id}")
    public ResponseEntity<Ads> updateAds(@PathVariable Integer id,
                                         @RequestBody CreateAds createAds,
                                         Authentication authentication) {
        return ResponseEntity.ok(adsService.updateAds(id, createAds, authentication));
    }

    @Operation(summary = "getComments", description = "Запрос комментария пользователя",
            tags = {"Объявления"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Comment.class))),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @GetMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> getComments(@PathVariable Integer adId,
                                               @PathVariable Integer commentId) {
        return ResponseEntity.ok(commentService.getComment(adId, commentId));
    }

    @Operation(summary = "deleteComments", description = "Удаление комментария",
            tags = {"Объявления"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComments(@PathVariable Integer adId,
                                               @PathVariable Integer commentId) {
        commentService.deleteComment(adId, commentId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "updateComment", description = "Обновление комментария пользователя",
            tags = {"Объявления"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленный комментарий",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Comment.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Comment.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Integer adId,
                                                 @PathVariable Integer commentId,
                                                 @RequestBody Comment comment,
                                                 Authentication authentication) {
        return ResponseEntity.ok(commentService.updateComment(adId, commentId, comment, authentication));
    }

    @Operation(summary = "getAdsMe", description = "Запрос списка всех объявлений пользователя",
            tags = {"Объявления"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseWrapperAds.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    @GetMapping("/me")
    public ResponseEntity<ResponseWrapperAds> getAdsMe(Authentication authentication) {
        return ResponseEntity.ok(adsService.getAdsMe(authentication));
    }


    @Operation(summary = "updateAdsImage", description = "Обновление картинки объявления",
            tags = {"Объявления"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новая картинка объявления",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateAdsImage(@PathVariable Integer id,
                                                 @RequestPart MultipartFile image) {

        return ResponseEntity.ok().build();
    }

}

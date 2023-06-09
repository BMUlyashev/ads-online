package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.AvatarService;
import ru.skypro.homework.service.UserService;

import java.io.IOException;


@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AvatarService avatarService;

    @Operation(summary = "getUser", description = "Получение пользователя", tags = {"Пользователи"},
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @GetMapping("/me")
    public ResponseEntity<User> getUsers(Authentication authentication) {
        return ResponseEntity.ok(userService.getUsers(authentication));
    }

    @Operation(summary = "updateUser", description = "Обновление данных пользователя", tags = {"Пользователи"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные пользователя",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = User.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @PatchMapping("/me")
    public ResponseEntity<User> updateUser(@RequestBody User user, Authentication authentication) {
        return ResponseEntity.ok(userService.updateUser(user, authentication));
    }

    @Operation(summary = "setPassword", description = "Изменение пароля пользователя", tags = {"Пользователи"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новый пароль",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NewPassword.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = NewPassword.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @PostMapping("/set_password")
    public ResponseEntity<NewPassword> setPassword(@RequestBody NewPassword newPassword, Authentication authentication) {
        return ResponseEntity.ok(userService.setPassword(newPassword, authentication));
    }

    @Operation(summary = "updateUserImage", description = "Обновление аватара пользователя", tags = {"Пользователи"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новый аватар пользователя",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateUserImage(@RequestPart MultipartFile image, Authentication authentication) throws IOException {
        avatarService.uploadAvatar(image, authentication);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "getUserImage", description = "Получение аватара пользователя", tags = {"Пользователи"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
                    @ApiResponse(responseCode = "404", description = "Not found")
            })
    @GetMapping("/me/image/{id}")
    public ResponseEntity<byte[]> loadImage(@PathVariable Integer id) throws IOException {
        Pair<String, byte[]> content = avatarService.readAvatar(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content.getFirst()))
                .contentLength(content.getSecond().length)
                .body(content.getSecond());
    }
}

package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/avatar")
@Tag(name = "AvatarController", description = "Аватар Студентов")
public class AvatarController {
    private AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @Operation(summary = "Добавить Аватар")
    @PostMapping(value = "/{Id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long Id, @RequestParam MultipartFile avatar) throws IOException {
        avatarService.uploadAvatar(Id, avatar);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Превью Аватар - Из Базы данных")
    @GetMapping(value = "/{Id}/avatar/preview")
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable Long Id) {

        Avatar avatar = avatarService.findAvatar(Id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(avatar.getData());

    }

    @Operation(summary = "Аватар - Из файловой системы")
    @GetMapping(value = "/{Id}/avatar")
    public void downloadAvatar(@PathVariable Long Id, HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.findAvatar(Id);

        Path path = Path.of(avatar.getFilePath());

        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream()) {
            response.setStatus(200);
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            is.transferTo(os);
        }
    }
}
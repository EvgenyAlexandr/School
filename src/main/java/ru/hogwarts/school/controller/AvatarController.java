package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
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

    @Operation(summary = "Аватары Всех студентов используя Пагинацию (Постранично)")
    @GetMapping
    public ResponseEntity<Page<Avatar>> getAllAvatars(
            @RequestParam(defaultValue = "0") int page,     // page - Номер страницы (по умолчанию 0), size - Размер страницы (по умолчанию 4)
            @RequestParam(defaultValue = "8") int size ) {

                // Проверка корректности номера страницы
                if (page < 0) {
                    return ResponseEntity.badRequest().build();  // Возвращаем 400 Bad Request при отрицательном значении
                }

                // Валидация размера страницы: если <=0 или >50, устанавливаем значение по умолчанию (8)
                if (size <= 0 || size > 50) {
                    size = 8;
                }

                // Получение страницы Аватаров из сервиса
                Page<Avatar> avatars = avatarService.getAllAvatars(page, size);

                // Если данных на странице нет, возвращаем 204 No Content
                if (avatars.isEmpty()) {
                    return ResponseEntity.noContent().build();
                }

                // Если данные есть, возвращаем 200 OK с телом ответа (страница Аватаров)
                return ResponseEntity.ok(avatars);
    }
}
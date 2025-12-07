package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.*;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;

import static java.nio.file.StandardOpenOption.CREATE_NEW;


@Service
@Transactional
public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${path.to.avatars.folder}")         // Аннотация - Место где хранятся все Аватар студентов
    private String avatarsDir;

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);  // Logger из библиотеки org.slf4j

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
        logger.debug("AvatarService инициализирован");
    }


    // Загрузка Аватар студента из общей папки
    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        logger.info("Начало загрузки аватара для студента с ID: {}", studentId);
        logger.debug("Размер файла: {} байт, тип контента: {}",
                avatarFile.getSize(), avatarFile.getContentType());

        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> {
                        String errorMessage = String.format("Студент с ID %d не найден", studentId);
                        logger.error(errorMessage);
                        return new IllegalArgumentException(errorMessage);
                    });

            String extension = getExtension(avatarFile.getOriginalFilename());
            logger.debug("Расширение файла: {}", extension);

            Path filePath = Path.of(avatarsDir, studentId + "." + extension);
            logger.info("Путь сохранения файла: {}", filePath);

            // Создание директории если не существует
            Files.createDirectories(filePath.getParent());
            logger.debug("Директория создана или уже существует: {}", filePath.getParent());

            // Удаление старого файла если существует
            boolean fileDeleted = Files.deleteIfExists(filePath);
            if (fileDeleted) {
                logger.info("Старый файл аватара удален: {}", filePath);
            }

            // Сохранение файла на диск
            try (InputStream is = avatarFile.getInputStream();
                 OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                 BufferedInputStream bis = new BufferedInputStream(is, 1024);
                 BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
                bis.transferTo(bos);
                logger.info("Файл успешно сохранен на диск: {}", filePath);
            }

            // Поиск или создание аватара
            Avatar avatar = findAvatar(studentId);
            if (avatar.getId() == null) {
                logger.debug("Создание нового аватара для студента {}", studentId);
            } else {
                logger.debug("Обновление существующего аватара с ID: {}", avatar.getId());
            }

            avatar.setStudent(student);
            avatar.setFilePath(filePath.toString());
            avatar.setFileSize(avatarFile.getSize());
            avatar.setMediaType(avatarFile.getContentType());

            logger.debug("Генерация превью для базы данных...");
            byte[] previewData = generateDataForDB(filePath);
            avatar.setData(previewData);
            logger.debug("Превью сгенерировано, размер: {} байт", previewData.length);

            // Сохранение в базу данных
            Avatar savedAvatar = avatarRepository.save(avatar);
            logger.info("Аватар успешно сохранен в БД с ID: {} для студента {}",
                    savedAvatar.getId(), studentId);

        } catch (Exception e) {
            logger.error("Ошибка при загрузке аватара для студента {}: {}", studentId, e.getMessage(), e);
            throw e;
        }
    }

    // Загрузка превью Аватар из Базы
    private byte[] generateDataForDB(Path filePath) throws IOException {
        logger.debug("Начало генерации превью для файла: {}", filePath);

        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                logger.error("Не удалось прочитать изображение: {}", filePath);
                throw new IOException("Неверный формат изображения");
            }

            logger.debug("Изображение прочитано: ширина={}, высота={}, тип={}",
                    image.getWidth(), image.getHeight(), image.getType());

            // Расчет размеров превью
            int height = image.getHeight() / (image.getWidth() / 100);
            logger.debug("Размеры превью: ширина=100, высота={}", height);

            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics2D = preview.createGraphics();
            graphics2D.drawImage(image, 0, 0, 100, height, null);
            graphics2D.dispose();

            String extension = getExtension(filePath.getFileName().toString());
            ImageIO.write(preview, extension, baos);

            byte[] result = baos.toByteArray();
            logger.debug("Превью успешно сгенерировано, размер: {} байт", result.length);

            return result;

        } catch (Exception e) {
            logger.error("Ошибка генерации превью для {}: {}", filePath, e.getMessage(), e);
            throw e;
        }
    }

    // Поиск Аватар по ID студента
    public Avatar findAvatar(Long studentId) {
        logger.debug("Поиск аватара для студента с ID: {}", studentId);

        try {
            return avatarRepository.findByStudentId(studentId)
                    .orElseGet(() -> {
                        logger.debug("Аватар для студента {} не найден, создан новый объект", studentId);
                        return new Avatar();
                    });
        } catch (Exception e) {
            logger.error("Ошибка поиска аватара для студента {}: {}", studentId, e.getMessage(), e);
            return new Avatar();
        }
    }

    // Получит расширение файла (пример =>.jpeg)
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            logger.warn("Некорректное имя файла для получения расширения: {}", fileName);
            return "jpg";
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        logger.trace("Получено расширение '{}' из файла '{}'", extension, fileName);
        return extension;
    }

    // Все Аватар
    public Page<Avatar> getAllAvatars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Avatar> avatars = avatarRepository.findAll(pageable);
        logger.info("Получено {} аватаров для страницы {} (размер страницы: {})",
                avatars.getTotalElements(), page, size);
        return avatars;
    }


}
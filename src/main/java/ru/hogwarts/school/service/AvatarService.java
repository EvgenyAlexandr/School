package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
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
import java.util.Collection;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

//@Service
//public class StudentService {
//
//    @Value("${avatars.dir.path}")
//    private String avatarsDir;
//
//    private final StudentRepository studentRepository;
//    private final AvatarRepository avatarRepository;
//
//    public StudentService(StudentRepository studentRepository, AvatarRepository avatarRepository) {
//        this.studentRepository = studentRepository;
//        this.avatarRepository = avatarRepository;
//    }
//
//    public Student addStudent(Student student) {
//        student.setId(null);
//        return studentRepository.save(student);
//    }
//
//    public Student findStudent(long id) {
//        return studentRepository.findById(id).orElseThrow();
//    }
//
//    public Student editStudent(Student student) {
//        return studentRepository.save(student);
//    }
//
//    public void deleteStudent(long id) {
//        studentRepository.deleteById(id);
//    }
//
//    public Collection<Student> findByAge(int age) {
//        return studentRepository.findAllByAge(age);
//    }
//
//    public Avatar findAvatar(long studentId) {
//        return avatarRepository.findByStudentId(studentId).orElseThrow();
//    }
//
//    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
//        Student student = findStudent(studentId);
//
//        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(file.getOriginalFilename()));
//        Files.createDirectories(filePath.getParent());
//        Files.deleteIfExists(filePath);
//
//        try (InputStream is = file.getInputStream();
//             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
//             BufferedInputStream bis = new BufferedInputStream(is, 1024);
//             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
//        ) {
//            bis.transferTo(bos);
//        }
//
//        Avatar avatar = avatarRepository.findByStudentId(studentId).orElseGet(Avatar::new);
//        avatar.setStudent(student);
//        avatar.setFilePath(filePath.toString());
//        avatar.setFileSize(file.getSize());
//        avatar.setMediaType(file.getContentType());
//        avatar.setData(file.getBytes());
//
//        avatarRepository.save(avatar);
//    }
//
//    private String getExtension(String fileName) {
//        return fileName.substring(fileName.lastIndexOf(".") + 1);
//    }
//}


@Service
@Transactional
public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${path.to.avatars.folder}")         // Аннотация - Место где хранятся все Аватар студентов
    private String avatarsDir;


    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }


    // Отображение Аватар студента
//    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
//        Student student = studentRepository.getById(studentId);
//        Path filePath = Path.of(avatarsDir, student + "." + getExtensions(avatarFile.getOriginalFilename()));
//        Files.createDirectories(filePath.getParent());
//        Files.deleteIfExists(filePath);
//        try (
//                InputStream is = avatarFile.getInputStream();
//                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
//                BufferedInputStream bis = new BufferedInputStream(is, 1024);
//                BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
//        ) {
//            bis.transferTo(bos);
//        }
//        Avatar avatar = findAvatar(studentId);
//        avatar.setStudent(student);
//        avatar.setFilePath(filePath.toString());
//        avatar.setFileSize(avatarFile.getSize());
//        avatar.setMediaType(avatarFile.getContentType());
//        avatar.setData(avatarFile.getBytes());
//        avatarRepository.save(avatar);
//    }

//    // Поиск Аватар по ID студента
//    public Avatar findAvatar(Long studentId) {
//        return avatarRepository.findAvatarById(studentId).orElse(new Avatar());
//    }
//
//    // Получит расширение файла (что за файл, как с ним работать)
//    private String getExtensions(String fileName) {
//        return fileName.substring(fileName.lastIndexOf(".") + 1);
//    }
//
//    // Все Аватар
//    public Page<Avatar> getAllAvatars(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return avatarRepository.findAll(pageable);
//    }

    // Загрузка Аватар студента из общей папки
    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        Student student = studentRepository.getById(studentId);

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(avatarFile.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = avatarFile.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
            bis.transferTo(bos);
        }

        Avatar avatar = findAvatar(studentId);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(generateDataForDB(filePath));

        avatarRepository.save(avatar);
    }

    // Загрузка превью Аватар из Базы
    private byte[] generateDataForDB(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics2D = preview.createGraphics();
            graphics2D.drawImage(image, 0, 0, 100, height, null);
            graphics2D.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    // Поиск Аватар по ID студента
    public Avatar findAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }

    // Получит расширение файла (пример =>.jpeg)
    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }




}
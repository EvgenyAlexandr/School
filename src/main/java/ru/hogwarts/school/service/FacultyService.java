package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
        logger.info("FacultyService инициализирован");
    }

    // Добавить
    public Faculty addFaculty(Faculty faculty) {
        logger.info("Добавление нового факультета: {}", faculty.getName());
        logger.debug("Данные факультета: имя='{}', цвет='{}'",
                faculty.getName(), faculty.getColor());

        try {
            Faculty savedFaculty = facultyRepository.save(faculty);
            logger.info("Факультет успешно добавлен с ID: {}", savedFaculty.getId());
            logger.debug("Полные данные сохраненного факультета: {}", savedFaculty);
            return savedFaculty;
        } catch (Exception e) {
            logger.error("Ошибка при добавлении факультета '{}': {}",
                    faculty.getName(), e.getMessage(), e);
            throw e;
        }
    }

    // Найти факультет по ID
    public Faculty findFaculty(long id) {
        logger.debug("Поиск факультета по ID: {}", id);

        try {
            Optional<Faculty> facultyOptional = facultyRepository.findById(id);

            if (facultyOptional.isPresent()) {
                Faculty faculty = facultyOptional.get();
                logger.info("Факультет найден: ID={}, имя='{}', цвет='{}'",
                        id, faculty.getName(), faculty.getColor());
                return faculty;
            } else {
                logger.warn("Факультет с ID {} не найден", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Ошибка при поиске факультета с ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    // Редактировать
    public Faculty editFaculty(long id, Faculty updatedFaculty) {
        logger.info("Редактирование факультета с ID: {}", id);
        logger.debug("Новые данные: имя='{}', цвет='{}'",
                updatedFaculty.getName(), updatedFaculty.getColor());

        try {
            // 1. Находим существующую запись по ID
            Faculty existingFaculty = facultyRepository.findById(id)
                    .orElseThrow(() -> {
                        String errorMessage = "Факультет с ID " + id + " не найден";
                        logger.error(errorMessage);
                        return new EntityNotFoundException(errorMessage);
                    });

            logger.debug("Старые данные: имя='{}', цвет='{}'",
                    existingFaculty.getName(), existingFaculty.getColor());

            // 2. Обновляем поля
            existingFaculty.setName(updatedFaculty.getName());
            existingFaculty.setColor(updatedFaculty.getColor());

            Faculty savedFaculty = facultyRepository.save(existingFaculty);
            logger.info("Факультет с ID {} успешно обновлен", id);
            logger.debug("Обновленные данные факультета: {}", savedFaculty);

            return savedFaculty;
        } catch (EntityNotFoundException e) {
            // Логирование уже было в orElseThrow
            throw e;
        } catch (Exception e) {
            logger.error("Ошибка при редактировании факультета с ID {}: {}",
                    id, e.getMessage(), e);
            throw e;
        }
    }

    // Удалить
    public void deleteFaculty(long id) {
        logger.info("Удаление факультета с ID: {}", id);

        try {
            // Проверяем существование перед удалением
            if (facultyRepository.existsById(id)) {
                facultyRepository.deleteById(id);
                logger.info("Факультет с ID {} успешно удален", id);
            } else {
                logger.warn("Попытка удаления несуществующего факультета с ID: {}", id);
                throw new EntityNotFoundException("Факультет с ID " + id + " не найден");
            }
        } catch (EntityNotFoundException e) {
            logger.error("Факультет с ID {} не найден для удаления", id);
            throw e;
        } catch (Exception e) {
            logger.error("Ошибка при удалении факультета с ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    // Найти по Цвету
    public Collection<Faculty> findByColor(String color) {
        logger.info("Поиск факультетов по цвету: '{}'", color);

        if (color == null || color.trim().isEmpty()) {
            logger.warn("Попытка поиска по пустому цвету");
            return Collections.emptyList();
        }

        try {
            Collection<Faculty> faculties = facultyRepository.findByColor(color);
            logger.info("Найдено {} факультетов цвета '{}'", faculties.size(), color);
            logger.debug("Найденные факультеты: {}", faculties);
            return faculties;
        } catch (Exception e) {
            logger.error("Ошибка при поиске факультетов по цвету '{}': {}",
                    color, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Все Факультеты
    public Collection<Faculty> getAllFaculty() {
        logger.info("Получение всех факультетов");

        try {
            Collection<Faculty> faculties = facultyRepository.findAll();
            logger.info("Получено {} факультетов", faculties.size());

            if (logger.isDebugEnabled()) {
                // Логируем детали только если уровень DEBUG
                faculties.forEach(faculty ->
                        logger.debug("Факультет: ID={}, имя='{}', цвет='{}'",
                                faculty.getId(), faculty.getName(), faculty.getColor())
                );
            }

            return faculties;
        } catch (Exception e) {
            logger.error("Ошибка при получении всех факультетов: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Поиск Факультета по Названию ИЛИ цвету
    public Collection<Faculty> findByNameOrColor(String name, String color) {
        logger.info("Поиск факультетов по имени '{}' или цвету '{}'", name, color);

        try {
            Collection<Faculty> faculties = facultyRepository
                    .findByNameIgnoreCaseOrColorIgnoreCase(name, color);

            logger.info("Найдено {} факультетов по запросу (имя='{}', цвет='{}')",
                    faculties.size(), name, color);

            if (logger.isDebugEnabled() && !faculties.isEmpty()) {
                logger.debug("Результаты поиска:");
                faculties.forEach(faculty ->
                        logger.debug("  ID={}, имя='{}', цвет='{}'",
                                faculty.getId(), faculty.getName(), faculty.getColor())
                );
            } else if (faculties.isEmpty()) {
                logger.debug("Факультеты по запросу не найдены");
            }

            return faculties;
        } catch (Exception e) {
            logger.error("Ошибка при поиске факультетов (имя='{}', цвет='{}'): {}",
                    name, color, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
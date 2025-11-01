package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService {

    @Autowired
    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    // Добавить
    public Faculty addFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    // Найти факультет по ID
    public Faculty findFaculty(long id) {
        return facultyRepository.getReferenceById(id);
    }

    // Редактировать
    public Faculty editFaculty(long id, Faculty faculty) {
        if (!facultyRepository.existsById(faculty.getId())){
            throw new EntityNotFoundException("Факультет с ID " + id + " не найден");
        }
        return facultyRepository.save(faculty);
    }

    // Удалить
    public void deleteFaculty(long id) {
        facultyRepository.deleteById(id);
    }

    // Найти по Цвету
    public Collection<Faculty> findByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    // Все Факультеты
    public Collection<Faculty> getAllFaculty(){
        return facultyRepository.findAll();
    }

    // Поиск Факультета по Названию ИЛИ цвету
    public Collection<Faculty> findByNameOrColor(String name, String color) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }
}

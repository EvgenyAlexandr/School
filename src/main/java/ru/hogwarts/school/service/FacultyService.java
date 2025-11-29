package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService {

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
        return facultyRepository.findById(id).orElse(null);
    }

    // Редактировать
    public Faculty editFaculty(long id, Faculty updatedFaculty) {
        // 1. Находим существующую запись по ID
        Faculty existingFaculty = facultyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Факультет с ID " + id + " не найден"));

        // 2. Обновляем поля
        existingFaculty.setName (updatedFaculty.getName());
        existingFaculty.setColor(updatedFaculty.getColor());

        return facultyRepository.save(existingFaculty);
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

//package ru.hogwarts.school.service;
//
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import ru.hogwarts.school.model.Faculty;
//import ru.hogwarts.school.model.Student;
//import ru.hogwarts.school.repository.FacultyRepository;
//import ru.hogwarts.school.repository.StudentRepository;
//
//import java.util.Collection;
//import java.util.List;
//
//@Service
//public class FacultyService {
//
//    private final FacultyRepository facultyRepository;
//
//    @Autowired
//    private StudentRepository studentRepository;
//
//    public FacultyService(FacultyRepository facultyRepository) {
//        this.facultyRepository = facultyRepository;
//    }
//
//    // Добавить
//    public Faculty addFaculty(Faculty faculty) {
//        return facultyRepository.save(faculty);
//    }
//
//    // Найти факультет по ID
//    public Faculty findFaculty(long id) {
//        return facultyRepository.findById(id).orElse(null);
//    }
//
//    // Редактировать
//    public Faculty editFaculty(long id, Faculty updatedFaculty) {
//        // 1. Находим существующую запись по ID
//        Faculty existingFaculty = facultyRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Факультет с ID " + id + " не найден"));
//
//        // 2. Обновляем поля
//        existingFaculty.setName(updatedFaculty.getName());
//        existingFaculty.setColor(updatedFaculty.getColor());
//
//        return facultyRepository.save(existingFaculty);
//    }
//
//    // Удалить
//    public void deleteFaculty(long id) {
//        facultyRepository.deleteById(id);
//    }
//
//    // Найти по Цвету
//    public Collection<Faculty> findByColor(String color) {
//        return facultyRepository.findByColor(color);
//    }
//
//    // Все Факультеты
//    public Collection<Faculty> getAllFaculty() {
//        return facultyRepository.findAll();
//    }
//
//    // Поиск Факультета по Названию ИЛИ цвету
//    public Collection<Faculty> findByNameOrColor(String name, String color) {
//        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
//    }
//
//    // Получить всех студентов факультета
//    public List<Student> getStudents(Long facultyId) {
//        return studentRepository.findByFacultyId(facultyId);
//    }
//}


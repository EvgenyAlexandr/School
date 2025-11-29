package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Добавить
    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    // Найти по ID
    public Student findStudent(long id) {
        return studentRepository.findById(id).orElse(null);
    }

    // Найти по Имени
    public Student findByName(String name) {
        return studentRepository.findByNameContainsIgnoreCase(name);
    }

    // Редактировать
    public Student editStudent(long id, Student updatedStudent) {
        // 1. Находим существующую запись по ID
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Факультет с ID " + id + " не найден"));

        // 2. Обновляем поля
        existingStudent.setName(updatedStudent.getName());
        existingStudent.setAge (updatedStudent.getAge());

        return studentRepository.save(existingStudent);
    }

    // Удалить
    public void deleteStudent(long id) {
        studentRepository.deleteById(id);
    }

    // Поиск по Возрасту
    public Collection<Student> findByAge(int age) {
        return studentRepository.findByAge(age);
    }

    // Поиск в диапазоне Возрастов
    public Collection<Student> findByAgeBetween(int minAge, int maxAge) {
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    // Редактирование Факультета студента
    public Student addStudentToFaculty(Student student, Faculty faculty) {
        student.setFaculty(faculty);
        return studentRepository.save(student);
    }

    // Все студенты
    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}

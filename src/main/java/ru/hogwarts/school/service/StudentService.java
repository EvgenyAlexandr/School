package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;

@Service
public class StudentService {

    @Autowired
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Добавить
    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    // Найти
    public Student findStudent(long id) {
        return studentRepository.getReferenceById(id);
    }

    // Редактировать
    public Student editStudent(long id, Student student) {
        if (!studentRepository.existsById(student.getId())){
            throw new EntityNotFoundException("Студент с ID " + id + " не найден");
        }
        studentRepository.save(student);
        return student;
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

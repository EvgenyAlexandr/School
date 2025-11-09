package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    // Поиск по Возрасту
    List<Student> findByAge(int age);
    // Поиск в диапазоне Возрастов
    List<Student> findByAgeBetween(int minAge, int maxAge);
    // Поиск по имени
    Student findByNameContainsIgnoreCase(String name);
}

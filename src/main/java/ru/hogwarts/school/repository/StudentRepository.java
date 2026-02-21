package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    // Поиск по Возрасту
    List<Student> findByAge(int age);
    // Поиск в диапазоне Возрастов
    List<Student> findByAgeBetween(int minAge, int maxAge);
    // Поиск по имени
    Student findByNameContainsIgnoreCase(String name);

    // Запрос - Общее количество записей студентов => Общее количество студентов
    @Query(value = "SELECT COUNT(*) FROM hogwarts.public.student", nativeQuery = true)
    Integer getTotalCountOfStudents();

    // Запрос - Средний возраст студента.
    @Query(value = "SELECT AVG(age) FROM hogwarts.public.student", nativeQuery = true)
    double getAverageAgeOfStudents();

    // Запрос - Последние 5 записей
    @Query(value = "SELECT * FROM hogwarts.public.student ORDER BY id DESC LIMIT 5", nativeQuery = true)
    Collection<Student> getLastFiveStudents();

    // Получить всех студентов
    List<Student> findAll();
}

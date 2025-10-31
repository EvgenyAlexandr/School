package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Faculty;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    // Поиск по цвету
    List<Faculty> findByColor(String color);
    // Поиск по цвету без учета регистра
    List<Faculty> findByNameIgnoreCaseOrColorIgnoreCase(String name, String color);

}

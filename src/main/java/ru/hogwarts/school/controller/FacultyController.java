package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.*;


@RestController
@RequestMapping("/faculty")
@Tag(name = "FacultyService", description = "Факультеты")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @Operation(summary = "Найти Факультет по ID")
    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFacultyInfo(@PathVariable long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();   // Если студент не найден возвращаем ошибку 404
        }
        return ResponseEntity.ok(faculty);
    }

    @Operation(summary = "Добавить Факультет")
    @PostMapping            // Отправить
    public Faculty createFaculty(@RequestBody Faculty faculty) {return facultyService.addFaculty(faculty);}

    @Operation(summary = "Редактировать Факультет по id")
    @PutMapping("{id}")     // Редактировать
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty, @PathVariable Long id) {
        Faculty foundFaculty = facultyService.editFaculty(id, faculty);
        if (foundFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();   // Если факультет не найден возвращаем ошибку 404
        }
        return ResponseEntity.ok(foundFaculty);
    }

    @Operation(summary = "Удалить Факультет по id")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Поиск Факультета по Цвету")
    @GetMapping("/find")             // Поиск Факультета по цвету
    public ResponseEntity<Collection<Faculty>> findFaculties(@RequestParam(required = false) String color) {
        if (color != null && !color.isBlank()) {
            return ResponseEntity.ok(facultyService.findByColor(color));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Operation(summary = "Поиск факультета по названию ИЛИ цвету")
    @GetMapping("/search")  // Поиск Факультета по Названию ИЛИ цвету
    public ResponseEntity<Collection<Faculty>> searchFaculties(@RequestParam String searchQuery) {
        if (searchQuery != null && !searchQuery.isBlank()) {
            return ResponseEntity.ok(facultyService.findByNameOrColor(searchQuery, searchQuery));
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Поиск всех студентов курса (по id)")
    @GetMapping("{id}/students")    // Поиск
    public ResponseEntity<Collection<Student>> getFacultyStudents(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty.getStudents());
    }
}

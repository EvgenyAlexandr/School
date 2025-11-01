package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.*;


@RestController
@RequestMapping("/student")
@Tag(name = "StudentService", description = "Студенты")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // http://localhost:8080/swagger-ui.html
    @Operation(summary = "Найти Студента по ID")
    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();   // Если студент не найден возвращаем ошибку 404
        }
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Добавить Студента")
    @PostMapping            // Отправить
    public Student createStudent(@RequestBody Student student) {return studentService.addStudent(student);}

    @Operation(summary = "Редактировать Студента по ID")
    @PutMapping("{id}")     // Редактировать
    public ResponseEntity<Student> editStudent(@RequestBody Student student, @PathVariable Long id) {
        Student foundStudent = studentService.editStudent(id, student);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();   // Если студент не найден возвращаем ошибку 404
        }
        return ResponseEntity.ok(foundStudent);
    }

    @Operation(summary = "Удалить Студента по ID")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Поиск Студентов по Возрасту")
    @GetMapping             // Поиск по Возрасту
    public ResponseEntity<Collection<Student>> findStudents(@RequestParam(required = false) int age) {
        if (age > 0) {
            return ResponseEntity.ok(studentService.findByAge(age));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Operation(summary = "Поиск Студентов в диапазоне возрастов")
    @GetMapping("/filter")  // Поиск в диапазоне Возрастов
    public ResponseEntity<Collection<Student>> findStudentsByAgeRange(
            @RequestParam int minAge,
            @RequestParam int maxAge) {
        if (minAge > 0 && maxAge > minAge) {
            return ResponseEntity.ok(studentService.findByAgeBetween(minAge, maxAge));
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Все Студенты выбранного факультета")
    @GetMapping("{id}/faculty")     // Все студенты выбранного факультета
    public ResponseEntity<Faculty> getStudentFaculty(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null || student.getFaculty() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student.getFaculty());
    }
}

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
    @Operation(summary = "Найти Студента по Id")
    @GetMapping("{id}")     // http://localhost:8080/student/(*)
    public ResponseEntity<Student> getStudentInfo(@PathVariable long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();   // Если студент не найден возвращаем ошибку 404
        }
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Найти Студента по Имени")
    @GetMapping("/byName")  // http://localhost:8080/student/byName?name=(*)
    public ResponseEntity findStudentByName(@RequestParam String name) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(studentService.findByName(name));
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Добавить Студента")
    @PostMapping            // Отправить - http://localhost:8080/student(*)
    public Student createStudent(@RequestBody Student student) {return studentService.addStudent(student);}

    @Operation(summary = "Редактировать Студента по Id")
    @PutMapping("{id}")     // Редактировать - http://localhost:8080/student/(*)
    public ResponseEntity<Student> editStudent(@RequestBody Student student, @PathVariable Long id) {
        Student foundStudent = studentService.editStudent(id, student);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();   // Если студент не найден возвращаем ошибку 404
        }
        return ResponseEntity.ok(foundStudent);
    }

    @Operation(summary = "Удалить Студента по Id")
    @DeleteMapping("{id}")  //  http://localhost:8080/student/(*)
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Поиск Студентов по Возрасту")
    @GetMapping             // Поиск по Возрасту - http://localhost:8080/student?age=(*)
    public ResponseEntity<Collection<Student>> findStudents(@RequestParam(required = false) int age) {
        if (age > 0) {
            return ResponseEntity.ok(studentService.findByAge(age));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @Operation(summary = "Поиск Студентов в диапазоне возрастов")
    @GetMapping("/filter")  // Поиск в диапазоне Возрастов - http://localhost:8080/student/filter?minAge=10&maxAge=12
    public ResponseEntity<Collection<Student>> findStudentsByAgeRange(
            @RequestParam int minAge,
            @RequestParam int maxAge) {
        if (minAge > 0 && maxAge > minAge) {
            return ResponseEntity.ok(studentService.findByAgeBetween(minAge, maxAge));
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Название Факультета Студента (Студент ID)")
    @GetMapping("{id}/faculty")     // Все студенты выбранного факультета - http://localhost:8080/student/(*)/faculty
    public ResponseEntity<Faculty> getStudentFaculty(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null || student.getFaculty() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student.getFaculty());
    }

    @Operation(summary = "Общее количество студентов (Integer)")
    @GetMapping("/count")           // http://localhost:8080/student/count
    public Integer getTotalStudentCount() {
        return studentService.getTotalCountOfStudents();
    }

    @Operation(summary = "Средний возраст студента (double)")
    @GetMapping("/average-age")     // http://localhost:8080/student/average-age
    public double getStudentAverageAge() {
        return studentService.getAverageAgeOfStudents();
    }

    @Operation(summary = "Последние 5 записей студентов")
    @GetMapping("/last-five")       //  http://localhost:8080/student/last-five
    public Collection<Student> getLastStudents() {
        return studentService.getLastFiveStudents();
    }

    @Operation(summary = "Получить имена студентов, начинающиеся с буквы А")
    @GetMapping("/names-starting-with-a")  // http://localhost:8080/student/names-starting-with-a
    public ResponseEntity<List<String>> getStudentNamesStartingWithA() {
        List<String> names = studentService.getStudentNamesStartingWithA();
        return ResponseEntity.ok(names);
    }

    @Operation(summary = "Средний возраст всех студентов через Stream API")
    @GetMapping("/average-age-stream")     // http://localhost:8080/student/average-age-stream
    public ResponseEntity<Double> getStudentAverageAgeStream() {
        double averageAge = studentService.getStudentAverageAgeStream();
        return ResponseEntity.ok(averageAge);
    }

    @Operation(summary = "Вычислить сумму чисел от 1 до 1,000,000 (оптимизированная формула)")
    @GetMapping("/calculate-sum-formula")
    public ResponseEntity<Long> calculateSumFormula() {
        long result = studentService.calculateSumOptimized();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Вычислить сумму чисел от 1 до 1,000,000 (parallel stream)")
    @GetMapping("/calculate-sum-parallel")
    public ResponseEntity<Long> calculateSumParallel() {
        long result = studentService.calculateSumParallelStream();
        return ResponseEntity.ok(result);
    }
}

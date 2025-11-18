package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("RestTest Контроллера Student")
public class StudentControllerRestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;              // Класс для тестирования

    @Autowired
    private StudentController studentController;        // Тестируемый контроллер


    // Для очистки базы перед каждым тестом
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private FacultyRepository facultyRepository;

    @BeforeEach
    void setUp() {
        // Очищаем базу данных перед каждым тестом
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    @DisplayName("Контроллеры запускаются")
    void contextLoads() throws Exception {
        Assertions.assertThat(studentController).isNotNull();
        //Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    @DisplayName("Найти Студента по Id")
    void testGetStudentInfo() throws Exception {
        Student student = new Student("Тестовый_Студент2", 13);

        // Создаем запись в базе
        Student sentStudent = this.restTemplate.postForObject(
                "http://localhost:" + port + "/student", student, Student.class);

        // Проверяем наличие записи
        Student responseStudent = this.restTemplate.getForObject(
                "http://localhost:" + port + "/student/" + sentStudent.getId(),
                Student.class
        );

        Assertions.assertThat(responseStudent).isNotNull();
        Assertions
                .assertThat(responseStudent.getId())
                .isEqualTo(sentStudent.getId());
    }

    @Test
    @DisplayName("Найти Студента по Имени")
    void testFindStudentByName() throws Exception {
        Student student = new Student("Тестовый_Студент2", 13);

        // Создаем запись в базе
        this.restTemplate.postForObject(
                "http://localhost:" + port + "/student", student, Student.class);

        // Проверяем наличие записи
        Student responseStudent = this.restTemplate.getForObject(
                "http://localhost:" + port + "/student/byName?name=" + student.getName(),
                Student.class
        );

        Assertions.assertThat(responseStudent).isNotNull();
        Assertions
                .assertThat(responseStudent.getName())
                .isEqualTo(student.getName());
    }

    @Test
    @DisplayName("Добавление Студента")
    void testGetStudentByName() throws Exception {
        Student student = new Student("Тестовый_Студент1", 13);

        Assertions
                .assertThat(this.restTemplate.postForObject(
                        "http://localhost:" + port + "/student", student, String.class))
                .isNotNull();
    }

    @Test
    @DisplayName("Редактирование Студента")
    void testEditStudent() throws Exception {
        Student student = new Student("Тестовый_Студент1", 13);

        final String str = "-New";

        Student createdStudent = this.restTemplate.postForObject(
                "http://localhost:" + port + "/student", student, Student.class);

        Assertions.assertThat(createdStudent).isNotNull();

        createdStudent.setName(student.getName() + str);
        createdStudent.setAge(student.getAge() + 5);

        Assertions
                .assertThat(this.restTemplate.exchange(
                        "http://localhost:" + port + "/student/" + createdStudent.getId(),
                        HttpMethod.PUT,
                        new HttpEntity<>(createdStudent),
                        Student.class
                ).getBody())
                .isNotNull()
                .extracting(Student::getName, Student::getAge)
                .containsExactly(createdStudent.getName(), createdStudent.getAge());
    }

    @Test
    @DisplayName("Удаление Студента по Id")
    void testDeleteStudent() throws Exception {
        Student student = new Student("Тестовый_Студент1", 13);

        // Добавление студента в базу
        Student createdStudent = this.restTemplate.postForObject(
                "http://localhost:" + port + "/student", student, Student.class);

        Assertions.assertThat(createdStudent).isNotNull();
        Assertions.assertThat(createdStudent.getId()).isNotNull();

        // Удаление студента из базы
        this.restTemplate.delete("http://localhost:" + port + "/student/" + createdStudent.getId());

        // Проверяем отсутствие записи
        Assertions
                .assertThat(this.restTemplate.getForObject(
                        "http://localhost:" + port + "/student/" + createdStudent.getId(), Student.class))
                .isNull();
    }

    @Test
    @DisplayName("Поиск студента по возрасту")
    void testFindStudentsAge() throws Exception {
        Student student1 = new Student("Тестовый_Студент_1", 13);
        Student student2 = new Student("Тестовый_Студент_2", 10);

        // Добавление студента в базу
        this.restTemplate.postForObject(
                "http://localhost:" + port + "/student", student1, Student.class);
        this.restTemplate.postForObject(
                "http://localhost:" + port + "/student", student2, Student.class);

        // Получаем ответ
        Student[] students = this.restTemplate.getForObject(
                "http://localhost:" + port + "/student?age=" + student1.getAge(),
                Student[].class
        );

        // Проверяем
        Assertions
                .assertThat(students).isNotNull()
                .isNotEmpty()
                .extracting(Student::getAge)
                .contains(student1.getAge())
                .allMatch(age -> age == student1.getAge());
    }

    @Test
    @DisplayName("Поиск студентов в диапазоне возрастов")
    void testFindStudentsByAgeRange() throws Exception {
        Student student1 = new Student("Тестовый_Студент_1", 21);
        Student student2 = new Student("Тестовый_Студент_2", 22);
        Student student3 = new Student("Тестовый_Студент_3", 23);
        int minAge = 21;
        int maxAge = 22;

        // Добавление студента в базу
        this.restTemplate.postForObject(
                "http://localhost:" + port + "/student", student1, Student.class);
        this.restTemplate.postForObject(
                "http://localhost:" + port + "/student", student2, Student.class);
        this.restTemplate.postForObject(
                "http://localhost:" + port + "/student", student3, Student.class);

        // Получаем ответ
        Student[] students = this.restTemplate.getForObject(
                "http://localhost:" + port + "/student/filter?minAge=" + minAge + "&maxAge=" + maxAge,
                Student[].class
        );

        // Проверяем
        Assertions
                .assertThat(students).isNotNull()
                .isNotEmpty()
                .extracting(Student::getAge)
                .contains(student1.getAge())
                .allMatch(age -> age >= minAge && age <= maxAge);
    }

    @Test
    @DisplayName("Название Факультета Студента")
    void testGetStudentFaculty() throws Exception {
        Student student = new Student("Тестовый_Студент_1", 13);
        Faculty faculty = new Faculty("Черные Паруса", "Черный");

        // Добавление факультета
        Faculty createdFaculty = this.restTemplate.postForObject(
                "http://localhost:" + port + "/faculty", faculty, Faculty.class);

        Assertions.assertThat(createdFaculty).isNotNull();

        // До Заполняем поля Студента - Факультет
        student.setFaculty(createdFaculty);

        // Добавление студента
        Student createdStudent = this.restTemplate.postForObject(
                "http://localhost:" + port + "/student", student, Student.class);

        Assertions.assertThat(createdStudent).isNotNull();

        // Получаем Faculty
        Faculty outFaculty = this.restTemplate.getForObject(
                "http://localhost:" + port + "/student/" + createdStudent.getId() + "/faculty",
                Faculty.class
        );

        // Проверяем
        Assertions
                .assertThat(outFaculty)
                .isNotNull()
                .extracting(Faculty::getName)
                .isEqualTo(createdFaculty.getName());
    }
}
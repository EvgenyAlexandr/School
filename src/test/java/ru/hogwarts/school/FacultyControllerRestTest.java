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
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("RestTest Контролера Faculty")
public class FacultyControllerRestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        // Очищаем базу данных перед каждым тестом
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    @DisplayName("Контроллер запускается")
    void contextLoads() throws Exception {
        Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    @DisplayName("Найти факультет по Id")
    void testGetFacultyInfo() throws Exception {
        Faculty faculty = new Faculty("Черные Паруса", "Черный");

        // Создаем запись в базе
        Faculty sentFaculty = this.restTemplate.postForObject(
                "http://localhost:" + port + "/faculty", faculty, Faculty.class);

        // Проверяем наличие записи
        Faculty responseFaculty = this.restTemplate.getForObject(
                "http://localhost:" + port + "/faculty/" + sentFaculty.getId(),
                Faculty.class
        );

        Assertions.assertThat(responseFaculty).isNotNull();
        Assertions
                .assertThat(responseFaculty.getId())
                .isEqualTo(sentFaculty.getId());
    }

    @Test
    @DisplayName("Добавить Факультет")
    void testPostFaculty() throws Exception {
        Faculty faculty = new Faculty("Черные Паруса", "Черный");

        Faculty createdFaculty = this.restTemplate.postForObject(
                "http://localhost:" + port + "/faculty", faculty, Faculty.class);

        Assertions.assertThat(createdFaculty).isNotNull();
        Assertions.assertThat(createdFaculty.getId()).isNotNull();
    }

    @Test
    @DisplayName("Редактирование Факультета по Id")
    void testEditFaculty() throws Exception {
        final String str = "-New";

        Faculty faculty = new Faculty("Черные Паруса", "Черный");

        Faculty createdFaculty = this.restTemplate.postForObject(
                "http://localhost:" + port + "/faculty", faculty, Faculty.class);

        Assertions.assertThat(createdFaculty).isNotNull();

        createdFaculty.setName(faculty.getName() + str);
        createdFaculty.setColor(faculty.getColor() + str);

        Assertions
                .assertThat(this.restTemplate.exchange(
                        "http://localhost:" + port + "/faculty/" + createdFaculty.getId(),
                        HttpMethod.PUT,
                        new HttpEntity<>(createdFaculty),
                        Faculty.class
                ).getBody())
                .isNotNull()
                .extracting(Faculty::getName, Faculty::getColor)
                .containsExactly(faculty.getName() + str, faculty.getColor() + str);
    }

    @Test
    @DisplayName("Удаление Факультета по Id")
    void testDeleteFaculty() throws Exception {
        Faculty faculty = new Faculty("Черные Паруса", "Черный");

        Faculty createdFaculty = this.restTemplate.postForObject(
                "http://localhost:" + port + "/faculty", faculty, Faculty.class);

        Assertions.assertThat(createdFaculty).isNotNull();
        Assertions.assertThat(createdFaculty.getId()).isNotNull();

        this.restTemplate.delete("http://localhost:" + port + "/faculty/" + createdFaculty.getId());

        // Проверяем, что факультет действительно удален
        Faculty deletedFaculty = this.restTemplate.getForObject(
                "http://localhost:" + port + "/faculty/" + createdFaculty.getId(),
                Faculty.class
        );
        Assertions.assertThat(deletedFaculty).isNull();
    }

    @Test
    @DisplayName("Поиск факультета по цвету")
    void testGetFacultyByColor() throws Exception {
        Faculty faculty = new Faculty("Черные Паруса", "Черный");

        this.restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty, Faculty.class);

        // Ищем факультет по цвету - ожидаем массив
        Faculty[] faculties = this.restTemplate.getForObject(
                "http://localhost:" + port + "/faculty/find?color=" + faculty.getColor(),
                Faculty[].class
        );

        Assertions
                .assertThat(faculties)
                .isNotNull()
                .isNotEmpty()
                .extracting(Faculty::getColor)
                .contains(faculty.getColor());
    }

    @Test
    @DisplayName("Поиск факультета по Названию ИлИ Цвету")
    void testGetSearchFaculties() throws Exception {
        Faculty faculty = new Faculty("Черные Паруса", "Черный");

        this.restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty, Faculty.class);

        // Ищем факультет по Названию - ожидаем массив
        Faculty[] facultiesByName = this.restTemplate.getForObject(
                "http://localhost:" + port + "/faculty/search?searchQuery=" + faculty.getName(),
                Faculty[].class
        );

        Assertions
                .assertThat(facultiesByName)
                .isNotNull()
                .isNotEmpty()
                .extracting(Faculty::getName)
                .contains(faculty.getName());

        // Ищем факультет по Цвету - ожидаем массив
        Faculty[] facultiesByColor = this.restTemplate.getForObject(
                "http://localhost:" + port + "/faculty/search?searchQuery=" + faculty.getColor(),
                Faculty[].class
        );

        Assertions
                .assertThat(facultiesByColor)
                .isNotNull()
                .isNotEmpty()
                .extracting(Faculty::getColor)
                .contains(faculty.getColor());
    }

    @Test
    @DisplayName("Поиск всех студентов факультета")
    void testGetFacultyStudents() throws Exception {
        // Создаем факультет
        Faculty faculty = new Faculty("Черные Паруса", "Черный");
        Faculty createdFaculty = this.restTemplate.postForObject(
                "http://localhost:" + port + "/faculty", faculty, Faculty.class);
        Assertions.assertThat(createdFaculty).isNotNull();

        // Создаем студентов и привязываем их к факультету
        Student student1 = new Student("Тестовый_Студент_1", 13);
        student1.setFaculty(createdFaculty);

        Student student2 = new Student("Тестовый_Студент_2", 14);
        student2.setFaculty(createdFaculty);

        // Добавляем студентов
        this.restTemplate.postForObject("http://localhost:" + port + "/student", student1, Student.class);
        this.restTemplate.postForObject("http://localhost:" + port + "/student", student2, Student.class);

        // Получаем студентов факультета - ожидаем массив студентов
        Student[] students = this.restTemplate.getForObject(
                "http://localhost:" + port + "/faculty/" + createdFaculty.getId() + "/students",
                Student[].class
        );

        // Проверяем
        Assertions
                .assertThat(students)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .extracting(Student::getName)
                .contains("Тестовый_Студент_1", "Тестовый_Студент_2");
    }
}

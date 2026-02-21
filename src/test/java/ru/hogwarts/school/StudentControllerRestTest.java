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

import static org.junit.jupiter.api.Assertions.*;


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


    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private FacultyRepository facultyRepository;

    // Для очистки базы перед каждым тестом
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
//        Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    @DisplayName("Найти Студента по Id")
    void testGetStudentInfo() throws Exception {
        // Создаем запись в Тестовой Базе H2
        Student student = studentRepository.save(
                new Student("Тестовый_Студент2", 13)
        );

        // Проверяем наличие записи
        Student responseStudent = this.restTemplate.getForObject(
                "http://localhost:" + port + "/student/" + student.getId(),
                Student.class
        );

        Assertions.assertThat(responseStudent).isNotNull();
        Assertions
                .assertThat(responseStudent.getId())
                .isEqualTo(student.getId());    // Сравниваем Id
    }

    @Test
    @DisplayName("Найти Студента по Имени")
    void testFindStudentByName() throws Exception {
        // Создаем запись в Тестовой Базе H2
        Student student = studentRepository.save(
                new Student("Тестовый_Студент2", 13)
        );

        // Проверяем наличие записи
        Student responseStudent = this.restTemplate.getForObject(
                "http://localhost:" + port + "/student/byName?name=" + student.getName(),
                Student.class
        );

        Assertions.assertThat(responseStudent).isNotNull();
        Assertions
                .assertThat(responseStudent.getName())
                .isEqualTo(student.getName());  // Сравниваем Name
    }

    @Test
    @DisplayName("Добавление Студента")
    void testGetStudentByName() throws Exception {
        Student student = new Student("Тестовый_Студент1", 13);

        Student responseStudent = this.restTemplate.postForObject(
                "http://localhost:" + port + "/student", student, Student.class);


        Assertions
                .assertThat(responseStudent)
                .isNotNull();

        // Проверяем в базе, что студент добавился
        assertTrue(studentRepository.findById(responseStudent.getId()).isPresent());
    }

    @Test
    @DisplayName("Редактирование Студента")
    void testEditStudent() throws Exception {
        final String str = "-New";

        // Создаем запись в Тестовой Базе H2
        Student createdStudent = studentRepository.save(
                new Student("Тестовый_Студент2", 13)
        );

        Assertions.assertThat(createdStudent).isNotNull();

        createdStudent.setName(createdStudent.getName() + str);
        createdStudent.setAge(createdStudent.getAge() + 5);

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

        // Проверяем что данные действительно изменились
        Student updatedStudent = studentRepository.findById(createdStudent.getId()).orElse(null);
        assertNotNull(updatedStudent);
        assertEquals(createdStudent.getName(), updatedStudent.getName());
        assertEquals(createdStudent.getAge(),  updatedStudent.getAge());

    }

    @Test
    @DisplayName("Удаление Студента по Id")
    void testDeleteStudent() throws Exception {
        // Создаем запись в Тестовой Базе H2
        Student createdStudent = studentRepository.save(
                new Student("Тестовый_Студент1", 13)
        );

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
        // Создаем запись в Тестовой Базе H2
        Student student1 = studentRepository.save(
                new Student("Тестовый_Студент_1", 10)
        );
        Student student2 = studentRepository.save(
                new Student("Тестовый_Студент_2", 13)
        );

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
        int minAge = 21;
        int maxAge = 22;

        // Создаем запись в Тестовой Базе H2
        Student student1 = studentRepository.save(
                new Student("Тестовый_Студент_1", 21)
        );
        Student student2 = studentRepository.save(
                new Student("Тестовый_Студент_2", 22)
        );
        Student student3 = studentRepository.save(
                new Student("Тестовый_Студент_3", 23)
        );

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

        // Создаем запись в Тестовой Базе H2
        Faculty createdFaculty = facultyRepository.save(faculty);

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
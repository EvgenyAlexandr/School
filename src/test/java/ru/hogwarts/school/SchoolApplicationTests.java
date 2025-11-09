package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
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
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)		// Запускаем тестирование на рандомном порту
@ActiveProfiles("test")															// Профиль test
@DisplayName("Тест Web приложения")
class SchoolApplicationTests {

	@LocalServerPort
	private int port;		// Случайный порт для развертывания тестирования

	@Autowired				// Класс для тестирования
	private TestRestTemplate restTemplate;

	// Тестируемые контроллеры
	@Autowired
	private StudentController studentController;
	@Autowired
	private FacultyController facultyController;


	@Test
	@DisplayName("Контроллеры запускаются")
	void contextLoads() throws Exception {
		Assertions.assertThat(studentController).isNotNull();
		Assertions.assertThat(facultyController).isNotNull();
	}

	@Test
	@DisplayName("Добавление Студента")
	void testGetStudentByName() throws Exception {
		final String name = "Тестовый_Студент";
		final int    age  = 13;

		Student student = new Student();
		student.setName(name);
		student.setAge(age);

		// Добавление студента в базу
		this.restTemplate.postForObject("http://localhost:" + port + "/student", student, Student.class);

		// Проверяем что студент добавился
		Assertions
				.assertThat(this.restTemplate.getForObject(
						"http://localhost:" + port + "/student/byName?name=" + name,
						String.class
				))
				.isNotNull()
				.contains(name);
	}

	@Test
	@DisplayName("Поиск студента")
	void testPostStudent() throws Exception {
		final String name = "Тестовый_Студент2";
		final int    age  = 13;

		Student student = new Student();
		student.setName(name);
		student.setAge(age);

		Assertions
				.assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/student", student, String.class))
				.isNotNull();
	}

	@Test
	@DisplayName("Удаление Студента")
	void testDeleteStudent() throws Exception {
		final String name = "Тестовый_Студент3";
		final int    age  = 13;

		Student student = new Student();
		student.setName(name);
		student.setAge(age);

		// Добавление студента в базу
		Student createdStudent = this.restTemplate.postForObject(
				"http://localhost:" + port + "/student", student, Student.class);

		Assertions.assertThat(createdStudent).isNotNull();
		Assertions.assertThat(createdStudent.getId()).isNotNull();

		// Удаление студента из базы
		this.restTemplate.delete("http://localhost:" + port + "/student/" + createdStudent.getId());
	}

	@Test
	@DisplayName("Редактирование Студента")
	void testEditStudent() throws Exception {
		final String name = "Тестовый_Студент4";
		final int    age  = 13;

		final String str = "-New";		// Добавляемый префикс

		Student student = new Student();
		student.setName(name);
		student.setAge(age);

		Student createdStudent = this.restTemplate.postForObject(
				"http://localhost:" + port + "/student", student, Student.class);

		Assertions.assertThat(createdStudent).isNotNull();

		createdStudent.setName(name + str);
		createdStudent.setAge(15);

		Assertions
				.assertThat(this.restTemplate.exchange(
						"http://localhost:" + port + "/student/" + createdStudent.getId(),
						HttpMethod.PUT,
						new HttpEntity<>(createdStudent),
						Student.class
				).getBody())
				.isNotNull()
				.extracting(Student::getName, Student::getAge)
				.containsExactly(name + str, 15);
	}

	@Test
	@DisplayName("Создание Факультета")
	void testPostFaculty() throws Exception {
		Faculty faculty = new Faculty();
		faculty.setName("Черные Паруса");
		faculty.setColor("Черный");

		Assertions
				.assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty, String.class))
				.isNotNull();
	}

	@Test
	@DisplayName("Удаление Факультета")
	void testDeleteFaculty() throws Exception {
		Faculty faculty = new Faculty();
		faculty.setName("Черные Паруса");
		faculty.setColor("Черный");

		Faculty createdFaculty = this.restTemplate.postForObject(
				"http://localhost:" + port + "/faculty", faculty, Faculty.class);

		Assertions.assertThat(createdFaculty).isNotNull();
		Assertions.assertThat(createdFaculty.getId()).isNotNull();

		this.restTemplate.delete("http://localhost:" + port + "/faculty/" + createdFaculty.getId());
	}

	@Test
	@DisplayName("Изменение Факультета")
	void testEditFaculty() throws Exception {
		final String str = "-New";		// Добавляемый префикс

		Faculty faculty = new Faculty();
		faculty.setName("Черные Паруса");
		faculty.setColor("Черный");

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
	@DisplayName("Поиск факультета по цвету")
	void testGetFacultyByColor() throws Exception {
		Faculty faculty = new Faculty();
		faculty.setName("Черные Паруса");
		faculty.setColor("Черный");

		this.restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty, Faculty.class);

		// Ищем факультет по цвету
		Assertions
				.assertThat(this.restTemplate.getForObject(
						"http://localhost:" + port + "/faculty/find?color=" + faculty.getColor(),
						String.class
				))
				.isNotNull()
				.contains("Черный");
	}
}

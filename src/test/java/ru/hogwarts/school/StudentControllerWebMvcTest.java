package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@DisplayName("WebMvcTest Контроллера Student")
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    @Test
    @DisplayName("Контроллеры запускаются")
    void contextLoads() throws Exception {
        Assertions.assertThat(mockMvc).isNotNull();
    }

    @Test
    @DisplayName("Найти Студента по Id")
    void testGetStudentInfo() throws Exception {
        Student student = new Student("Тестовый_Студент2", 13);
        student.setId(1L);

        when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тестовый_Студент2"))
                .andExpect(jsonPath("$.age").value(13));
    }

    @Test
    @DisplayName("Найти Студента по Id - не найден")
    void testGetStudentInfoNotFound() throws Exception {
        when(studentService.findStudent(1L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Найти Студента по Имени")
    void testFindStudentByName() throws Exception {
        Student student = new Student("Тестовый_Студент2", 13);
        student.setId(1L);

        when(studentService.findByName("Тестовый_Студент2")).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/byName")
                        .param("name", "Тестовый_Студент2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тестовый_Студент2"))
                .andExpect(jsonPath("$.age").value(13));
    }

    @Test
    @DisplayName("Найти Студента по Имени - пустое имя")
    void testFindStudentByNameEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/student/byName")
                        .param("name", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Добавление Студента")
    void testCreateStudent() throws Exception {
        Student student = new Student("Тестовый_Студент1", 13);
        student.setId(1L);

        when(studentService.addStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тестовый_Студент1"))
                .andExpect(jsonPath("$.age").value(13));
    }

    @Test
    @DisplayName("Редактирование Студента")
    void testEditStudent() throws Exception {
        Student student = new Student("Тестовый_Студент1_New", 18);
        student.setId(1L);

        when(studentService.editStudent(anyLong(), any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тестовый_Студент1_New"))
                .andExpect(jsonPath("$.age").value(18));
    }

    @Test
    @DisplayName("Редактирование Студента - не найден")
    void testEditStudentNotFound() throws Exception {
        Student student = new Student("Тестовый_Студент1_New", 18);

        when(studentService.editStudent(anyLong(), any(Student.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Удаление Студента по Id")
    void testDeleteStudent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Поиск студента по возрасту")
    void testFindStudentsAge() throws Exception {
        Student student1 = new Student("Тестовый_Студент_1", 13);
        student1.setId(1L);
        Student student2 = new Student("Тестовый_Студент_2", 13);
        student2.setId(2L);

        when(studentService.findByAge(13)).thenReturn(Arrays.asList(student1, student2));

        mockMvc.perform(MockMvcRequestBuilders.get("/student")
                        .param("age", "13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].age").value(13))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].age").value(13));
    }

    @Test
    @DisplayName("Поиск студента по возрасту - некорректный возраст")
    void testFindStudentsAgeInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/student")
                        .param("age", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Поиск студентов в диапазоне возрастов")
    void testFindStudentsByAgeRange() throws Exception {
        Student student1 = new Student("Тестовый_Студент_1", 21);
        student1.setId(1L);
        Student student2 = new Student("Тестовый_Студент_2", 22);
        student2.setId(2L);

        when(studentService.findByAgeBetween(21, 22)).thenReturn(Arrays.asList(student1, student2));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/filter")
                        .param("minAge", "21")
                        .param("maxAge", "22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].age").value(21))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].age").value(22));
    }

    @Test
    @DisplayName("Поиск студентов в диапазоне возрастов - некорректные параметры")
    void testFindStudentsByAgeRangeInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/student/filter")
                        .param("minAge", "0")
                        .param("maxAge", "22"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Название Факультета Студента")
    void testGetStudentFaculty() throws Exception {
        Student student = new Student("Тестовый_Студент_1", 13);
        student.setId(1L);
        Faculty faculty = new Faculty("Черные Паруса", "Черный");
        faculty.setId(1L);
        student.setFaculty(faculty);

        when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Черные Паруса"))
                .andExpect(jsonPath("$.color").value("Черный"));
    }

    @Test
    @DisplayName("Название Факультета Студента - студент не найден")
    void testGetStudentFacultyStudentNotFound() throws Exception {
        when(studentService.findStudent(1L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1/faculty"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Название Факультета Студента - факультет не найден")
    void testGetStudentFacultyFacultyNotFound() throws Exception {
        Student student = new Student("Тестовый_Студент_1", 13);
        student.setId(1L);

        when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1/faculty"))
                .andExpect(status().isNotFound());
    }
}
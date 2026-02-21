package ru.hogwarts.school;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
@DisplayName("WebMvcTest Контроллера Faculty")
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacultyService facultyService;

    @MockitoBean
    private StudentService studentService;

    private Faculty faculty;
    private Student student1;
    private Student student2;

    @BeforeEach             // Выполняется перед каждым тестом
    void setUp() {
        faculty = new Faculty("Черные Паруса", "Черный");
        faculty.setId(1L);

        student1 = new Student("Тестовый_Студент_1", 13);
        student1.setId(1L);
        student1.setFaculty(faculty);

        student2 = new Student("Тестовый_Студент_2", 14);
        student2.setId(2L);
        student2.setFaculty(faculty);
    }

    @Test
    @DisplayName("Найти факультет по Id")
    void testGetFacultyInfo() throws Exception {
        when(facultyService.findFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Черные Паруса"))
                .andExpect(jsonPath("$.color").value("Черный"));

        verify(facultyService).findFaculty(1L);
    }

    @Test
    @DisplayName("Найти факультет по несуществующему Id")
    void testGetFacultyInfoNotFound() throws Exception {
        when(facultyService.findFaculty(999L)).thenReturn(null);

        mockMvc.perform(get("/faculty/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(facultyService).findFaculty(999L);
    }

    @Test
    @DisplayName("Добавить Факультет")
    void testPostFaculty() throws Exception {
        Faculty newFaculty = new Faculty("Черные Паруса", "Черный");
        Faculty savedFaculty = new Faculty("Черные Паруса", "Черный");
        savedFaculty.setId(1L);

        when(facultyService.addFaculty(any(Faculty.class))).thenReturn(savedFaculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Черные Паруса"))
                .andExpect(jsonPath("$.color").value("Черный"));

        verify(facultyService).addFaculty(any(Faculty.class));
    }

    @Test
    @DisplayName("Редактирование Факультета по Id")
    void testEditFaculty() throws Exception {
        Faculty updatedFaculty = new Faculty("Черные Паруса-New", "Черный-New");
        updatedFaculty.setId(1L);

        when(facultyService.editFaculty(eq(1L), any(Faculty.class))).thenReturn(updatedFaculty);

        mockMvc.perform(put("/faculty/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFaculty)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Черные Паруса-New"))
                .andExpect(jsonPath("$.color").value("Черный-New"));

        verify(facultyService).editFaculty(eq(1L), any(Faculty.class));
    }

    @Test
    @DisplayName("Удаление Факультета по Id")
    void testDeleteFaculty() throws Exception {
        when(facultyService.findFaculty(1L)).thenReturn(faculty);
        doNothing().when(facultyService).deleteFaculty(1L);

        mockMvc.perform(delete("/faculty/{id}", 1L))
                .andExpect(status().isOk());

        verify(facultyService).deleteFaculty(1L);
    }

    @Test
    @DisplayName("Удаление несуществующего Факультета")
    void testDeleteFacultyNotFound() throws Exception {
        when(facultyService.findFaculty(999L)).thenReturn(null);

        mockMvc.perform(delete("/faculty/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(facultyService, never()).deleteFaculty(999L);

    }

    @Test
    @DisplayName("Поиск факультета по цвету")
    void testGetFacultyByColor() throws Exception {
        Collection<Faculty> faculties = Arrays.asList(faculty);
        when(facultyService.findByColor("Черный")).thenReturn(faculties);

        mockMvc.perform(get("/faculty/find")
                        .param("color", "Черный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Черные Паруса"))
                .andExpect(jsonPath("$[0].color").value("Черный"));

        verify(facultyService).findByColor("Черный");
    }

    @Test
    @DisplayName("Поиск факультета по цвету - нет результатов")
    void testGetFacultyByColorEmpty() throws Exception {
        when(facultyService.findByColor("Белый")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/faculty/find")
                        .param("color", "Белый"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(facultyService).findByColor("Белый");
    }

    @Test
    @DisplayName("Поиск факультета по Названию ИлИ Цвету")
    void testGetSearchFaculties() throws Exception {
        Collection<Faculty> faculties = Arrays.asList(faculty);

        // Тест поиска по названию
        when(facultyService.findByNameOrColor("Черные Паруса", "Черные Паруса")).thenReturn(faculties);

        mockMvc.perform(get("/faculty/search")
                        .param("searchQuery", "Черные Паруса"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Черные Паруса"))
                .andExpect(jsonPath("$[0].color").value("Черный"));

        verify(facultyService).findByNameOrColor("Черные Паруса", "Черные Паруса");

        // Тест поиска по цвету
        when(facultyService.findByNameOrColor("Черный", "Черный")).thenReturn(faculties);

        mockMvc.perform(get("/faculty/search")
                        .param("searchQuery", "Черный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Черные Паруса"))
                .andExpect(jsonPath("$[0].color").value("Черный"));

        verify(facultyService).findByNameOrColor("Черный", "Черный");
    }

    @Test
    @DisplayName("Поиск всех студентов факультета")
    void testGetFacultyStudents() throws Exception {
        // Создаем факультет со списком студентов
        faculty.setStudents(Arrays.asList(student1, student2));

        when(facultyService.findFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/{id}/students", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Тестовый_Студент_1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Тестовый_Студент_2"));

        verify(facultyService).findFaculty(1L);
    }

    @Test
    @DisplayName("Поиск студентов несуществующего факультета")
    void testGetFacultyStudentsNotFound() throws Exception {
        when(facultyService.findFaculty(999L)).thenReturn(null);

        mockMvc.perform(get("/faculty/{id}/students", 999L))
                .andExpect(status().isNotFound());

        verify(facultyService).findFaculty(999L);
        verify(studentService, never()).getAllStudents();
    }

    @Test
    @DisplayName("Редактирование с несовпадающим ID в теле и пути")
    void testEditFacultyWithMismatchedId() throws Exception {
        Faculty updatedFaculty = new Faculty("Черные Паруса-New", "Черный-New");
        updatedFaculty.setId(2L); // ID в теле не совпадает с ID в пути

        mockMvc.perform(put("/faculty/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFaculty)))
                .andExpect(status().isBadRequest());

        verify(facultyService, never()).editFaculty(anyLong(), any(Faculty.class));
    }
}
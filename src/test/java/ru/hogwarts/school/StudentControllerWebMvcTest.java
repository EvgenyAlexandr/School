package ru.hogwarts.school;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;

import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(controllers = StudentController.class)
//@Import(StudentService.class)
@DisplayName("WebMvcTest - Student")
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    private Student createTestStudent() {
        Student student = new Student();
        student.setId(1L);
        student.setName("Тест Тестовый");
        student.setAge(14);
        return student;
    }

//    @Test
//    @DisplayName("Добавление Студента")
//    public void testGetStudentInfo() throws Exception {
//        Student student = createTestStudent();
//
//        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/student/{id}", student.getId())
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(student.getId()))
//                .andExpect(jsonPath("$.name").value(student.getName()))
//                .andExpect(jsonPath("$.age").value(student.getAge()));
//    }



}
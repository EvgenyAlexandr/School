package ru.hogwarts.school.service;

import ru.hogwarts.school.entities.Student;
import ru.hogwarts.school.entities.Student;

public interface StudentService {

    Student addStudent (Student student);

    Student findStudent (long id);

    Student editStudent(long id, Student student);

    void deleteStudent(long id);



}

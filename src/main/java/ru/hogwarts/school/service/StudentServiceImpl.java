package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.entities.Student;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@Service
public class StudentServiceImpl {

    private final HashMap<Long, Student> students = new HashMap<>();
    private long count = 0;

    // Добавить
    public Student addStudent(Student student) {
        student.setId(count++);
        students.put(student.getId(), student);
        return student;
    }

    // Найти
    public Student findStudent(long id) {
        return students.get(id);
    }

    // Редактировать
    public Student editStudent(long id, Student student) {
        if (!students.containsKey(id)) {    // Если ID не существует
            return null;
        }
        students.put(id,student);
        return student;
    }

    // Удалить
    public void deleteStudent(long id) {
        students.remove(id);
    }

    // Поиск по Возрасту
    public Collection<Student> findByAge(int age) {
        ArrayList<Student> result = new ArrayList<>();
        for (Student student : students.values()) {
            if (student.getAge() == age) {
                result.add(student);
            }
        }
        return result;
    }
}

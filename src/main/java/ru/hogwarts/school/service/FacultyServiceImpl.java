package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.entities.Faculty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

@Service
public class FacultyServiceImpl {

    private final HashMap<Long, Faculty> faculties = new HashMap<>();   // Карта факультетов
    private long count = 0;

    // Добавить
    public Faculty addFaculty(Faculty faculty) {
        faculty.setId(count++);
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    // Найти
    public Faculty findFaculty(long id) {
        return faculties.get(id);
    }

    // Редактировать
    public Faculty editFaculty(long id, Faculty faculty) {
        if (!faculties.containsKey(id)) {    // Если ID не существует
            return null;
        }
        faculties.put(id,faculty);
        return faculty;
    }

    // Удалить
    public void deleteFaculty(long id) {
        faculties.remove(id);
    }

    // Найти по Цвету
    public Collection<Faculty> findByColor(String color) {
        ArrayList<Faculty> result = new ArrayList<>();
        for (Faculty faculty : faculties.values()) {
            if (Objects.equals(faculty.getColor(), color)) {
                result.add(faculty);
            }
        }
        return result;
    }

}

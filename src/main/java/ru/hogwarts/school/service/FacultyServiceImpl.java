package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.entities.Faculty;

import java.util.HashMap;

@Service
public class FacultyServiceImpl implements FacultyService {

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

}

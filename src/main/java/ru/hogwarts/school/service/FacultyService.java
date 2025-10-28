package ru.hogwarts.school.service;

import ru.hogwarts.school.entities.Faculty;

import java.util.Collection;

public interface FacultyService {

    Faculty addFaculty (Faculty faculty);

    Faculty findFaculty (long id);

    Faculty editFaculty(long id, Faculty faculty);

    void deleteFaculty(long id);

    public Collection<Faculty> findByColor(String color);
}

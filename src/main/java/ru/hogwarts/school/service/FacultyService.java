package ru.hogwarts.school.service;

import ru.hogwarts.school.entities.Faculty;

public interface FacultyService {

    Faculty addFaculty (Faculty faculty);

    Faculty findFaculty (long id);

    Faculty editFaculty(long id, Faculty faculty);

    void deleteFaculty(long id);
}

package ru.hogwarts.school.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    // Поиск Аватара по ID студента
    //Optional<Avatar> findAvatarById(Long studentId);
    Optional<Avatar> findByStudentId(Long studentId);

    Page<Avatar> findAll(Pageable pageable);
}

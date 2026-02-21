-- Все студенты + Название факультетов
SELECT
    s.name AS Имя_студента,
    s.age  AS Возраст,
    f.name AS Название_факультета
FROM hogwarts.public.student s
JOIN hogwarts.public.faculty f ON s.faculty_id = f.id;

-- Все студенты у которых есть Аватар
SELECT
    s.name AS Имя_студента,
    s.age  AS Возраст,
    f.name AS Название_факультета
FROM hogwarts.public.student s
JOIN hogwarts.public.faculty f ON s.faculty_id = f.id
JOIN hogwarts.public.avatar a ON s.id = a.student_id;
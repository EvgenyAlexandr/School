package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    // Объект для синхронизации
    private final Object lock = new Object();

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        logger.info("StudentService инициализирован");
    }

    // Добавить
    public Student addStudent(Student student) {
        logger.info("Добавление нового студента: {}", student.getName());
        logger.debug("Данные студента: имя='{}', возраст={}, факультет={}",
                student.getName(), student.getAge(),
                student.getFaculty() != null ? student.getFaculty().getName() : "не указан");

        try {
            Student savedStudent = studentRepository.save(student);
            logger.info("Студент успешно добавлен с ID: {}", savedStudent.getId());
            logger.debug("Полные данные сохраненного студента: {}", savedStudent);
            return savedStudent;
        } catch (Exception e) {
            logger.error("Ошибка при добавлении студента '{}': {}",
                    student.getName(), e.getMessage(), e);
            throw e;
        }
    }

    // Найти по ID
    public Student findStudent(long id) {
        logger.debug("Поиск студента по ID: {}", id);

        try {
            Optional<Student> studentOptional = studentRepository.findById(id);

            if (studentOptional.isPresent()) {
                Student student = studentOptional.get();
                logger.info("Студент найден: ID={}, имя='{}', возраст={}",
                        id, student.getName(), student.getAge());
                return student;
            } else {
                logger.warn("Студент с ID {} не найден", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Ошибка при поиске студента с ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    // Найти по Имени
    public Student findByName(String name) {
        logger.info("Поиск студента по имени: '{}'", name);

        if (name == null || name.trim().isEmpty()) {
            logger.warn("Попытка поиска по пустому имени");
            return null;
        }

        try {
            Student student = studentRepository.findByNameContainsIgnoreCase(name);
            if (student != null) {
                logger.info("Студент найден по имени '{}': ID={}, возраст={}",
                        name, student.getId(), student.getAge());
            } else {
                logger.debug("Студент с именем '{}' не найден", name);
            }
            return student;
        } catch (Exception e) {
            logger.error("Ошибка при поиске студента по имени '{}': {}",
                    name, e.getMessage(), e);
            return null;
        }
    }

    // Редактировать
    public Student editStudent(long id, Student updatedStudent) {
        logger.info("Редактирование студента с ID: {}", id);
        logger.debug("Новые данные: имя='{}', возраст={}",
                updatedStudent.getName(), updatedStudent.getAge());

        try {
            // 1. Находим существующую запись по ID
            Student existingStudent = studentRepository.findById(id)
                    .orElseThrow(() -> {
                        String errorMessage = "Студент с ID " + id + " не найден";
                        logger.error(errorMessage);
                        return new EntityNotFoundException(errorMessage);
                    });

            logger.debug("Старые данные: имя='{}', возраст={}",
                    existingStudent.getName(), existingStudent.getAge());

            // 2. Обновляем поля
            existingStudent.setName(updatedStudent.getName());
            existingStudent.setAge(updatedStudent.getAge());

            Student savedStudent = studentRepository.save(existingStudent);
            logger.info("Студент с ID {} успешно обновлен", id);
            logger.debug("Обновленные данные студента: {}", savedStudent);

            return savedStudent;
        } catch (EntityNotFoundException e) {
            // Логирование уже было в orElseThrow
            throw e;
        } catch (Exception e) {
            logger.error("Ошибка при редактировании студента с ID {}: {}",
                    id, e.getMessage(), e);
            throw e;
        }
    }

    // Удалить
    public void deleteStudent(long id) {
        logger.info("Удаление студента с ID: {}", id);

        try {
            // Проверяем существование перед удалением
            if (studentRepository.existsById(id)) {
                // Получаем информацию о студенте для логирования
                studentRepository.findById(id).ifPresent(student ->
                        logger.debug("Удаляемый студент: имя='{}', возраст={}",
                                student.getName(), student.getAge())
                );

                studentRepository.deleteById(id);
                logger.info("Студент с ID {} успешно удален", id);
            } else {
                logger.warn("Попытка удаления несуществующего студента с ID: {}", id);
                throw new EntityNotFoundException("Студент с ID " + id + " не найден");
            }
        } catch (EntityNotFoundException e) {
            logger.error("Студент с ID {} не найден для удаления", id);
            throw e;
        } catch (Exception e) {
            logger.error("Ошибка при удалении студента с ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    // Поиск по Возрасту
    public Collection<Student> findByAge(int age) {
        logger.info("Поиск студентов по возрасту: {}", age);

        if (age <= 0) {
            logger.warn("Попытка поиска по некорректному возрасту: {}", age);
            return Collections.emptyList();
        }

        try {
            Collection<Student> students = studentRepository.findByAge(age);
            logger.info("Найдено {} студентов возраста {}", students.size(), age);

            if (logger.isDebugEnabled() && !students.isEmpty()) {
                logger.debug("Студенты возраста {}:", age);
                students.forEach(student ->
                        logger.debug("  ID={}, имя='{}'", student.getId(), student.getName())
                );
            }

            return students;
        } catch (Exception e) {
            logger.error("Ошибка при поиске студентов возраста {}: {}",
                    age, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Поиск в диапазоне Возрастов
    public Collection<Student> findByAgeBetween(int minAge, int maxAge) {
        logger.info("Поиск студентов по диапазону возраста: от {} до {}", minAge, maxAge);

        if (minAge > maxAge) {
            logger.warn("Некорректный диапазон возраста: min={} > max={}", minAge, maxAge);
            int temp = minAge;
            minAge = maxAge;
            maxAge = temp;
            logger.debug("Исправленный диапазон: от {} до {}", minAge, maxAge);
        }

        try {
            Collection<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);
            logger.info("Найдено {} студентов в возрасте от {} до {}",
                    students.size(), minAge, maxAge);

            if (logger.isDebugEnabled() && !students.isEmpty()) {
                logger.debug("Студенты в диапазоне {} - {}:", minAge, maxAge);
                students.forEach(student ->
                        logger.debug("  ID={}, имя='{}', возраст={}",
                                student.getId(), student.getName(), student.getAge())
                );
            }

            return students;
        } catch (Exception e) {
            logger.error("Ошибка при поиске студентов в диапазоне {} - {}: {}",
                    minAge, maxAge, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Редактирование Факультета студента
    public Student addStudentToFaculty(Student student, Faculty faculty) {
        if (student == null || faculty == null) {
            logger.error("Ошибка: студент или факультет равен null");
            throw new IllegalArgumentException("Студент и факультет не могут быть null");
        }

        logger.info("Добавление студента ID={} ('{}') на факультет '{}'",
                student.getId(), student.getName(), faculty.getName());

        try {
            student.setFaculty(faculty);
            Student savedStudent = studentRepository.save(student);
            logger.info("Студент ID={} успешно добавлен на факультет '{}'",
                    savedStudent.getId(), faculty.getName());
            return savedStudent;
        } catch (Exception e) {
            logger.error("Ошибка при добавлении студента на факультет: {}", e.getMessage(), e);
            throw e;
        }
    }

    // Все студенты
    public Collection<Student> getAllStudents() {
        logger.info("Получение всех студентов");

        try {
            Collection<Student> students = studentRepository.findAll();
            logger.info("Получено {} студентов", students.size());

            if (logger.isDebugEnabled()) {
                // Логируем детали только если уровень DEBUG
                students.forEach(student ->
                        logger.debug("Студент: ID={}, имя='{}', возраст={}, факультет={}",
                                student.getId(), student.getName(), student.getAge(),
                                student.getFaculty() != null ? student.getFaculty().getName() : "не указан")
                );
            }

            return students;
        } catch (Exception e) {
            logger.error("Ошибка при получении всех студентов: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Общее количество студентов
    public Integer getTotalCountOfStudents() {
        logger.debug("Получение общего количества студентов");

        try {
            Integer count = studentRepository.getTotalCountOfStudents();
            if (count == null) {
                count = 0;
                logger.warn("Метод getTotalCountOfStudents вернул null, установлено значение 0");
            }
            logger.info("Общее количество студентов: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Ошибка при получении общего количества студентов: {}", e.getMessage(), e);
            return 0;
        }
    }

    // Средний возраст студента
    public double getAverageAgeOfStudents() {
        logger.debug("Получение среднего возраста студентов");

        try {
            Double averageAge = studentRepository.getAverageAgeOfStudents();
            if (averageAge == null) {
                averageAge = 0.0;
                logger.warn("Метод getAverageAgeOfStudents вернул null, установлено значение 0.0");
            }
            logger.info("Средний возраст студентов: {}.2f", averageAge);
            return averageAge;
        } catch (Exception e) {
            logger.error("Ошибка при получении среднего возраста студентов: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    // Последние 5 записей
    public Collection<Student> getLastFiveStudents() {
        logger.info("Получение последних 5 студентов");

        try {
            Collection<Student> students = studentRepository.getLastFiveStudents();
            logger.info("Получено {} последних студентов", students.size());

            if (logger.isDebugEnabled() && !students.isEmpty()) {
                logger.debug("Последние студенты:");
                students.forEach(student ->
                        logger.debug("  ID={}, имя='{}', возраст={}",
                                student.getId(), student.getName(), student.getAge())
                );
            }

            return students;
        } catch (Exception e) {
            logger.error("Ошибка при получении последних 5 студентов: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Получить имена студентов, начинающиеся с буквы А
    public List<String> getStudentNamesStartingWithA() {
        logger.info("Получение имен студентов, начинающихся с буквы 'А'");

        try {
            List<String> names = studentRepository.findAll().stream()
                    .map(Student::getName)                                          // Получаем имена
                    .filter(name -> name != null && !name.trim().isEmpty())   // Фильтруем непустые имена
                    .filter(name -> name.toUpperCase().startsWith("А"))       // Фильтруем имена, начинающиеся с "А"
                    .map(String::toUpperCase)                                       // Приводим к верхнему регистру
                    .sorted()                                                       // Сортируем по алфавиту
                    .collect(Collectors.toList());

            logger.info("Найдено {} студентов с именами, начинающимися с 'А'", names.size());

            if (logger.isDebugEnabled() && !names.isEmpty()) {
                logger.debug("Имена студентов, начинающиеся с 'А':");
                names.forEach(name -> logger.debug("  {}", name));
            }

            return names;
        } catch (Exception e) {
            logger.error("Ошибка при получении имен студентов, начинающихся с 'А': {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Средний возраст всех студентов через Stream API
    public double getStudentAverageAgeStream() {
        logger.info("Расчет среднего возраста студентов через Stream API");

        try {
            List<Student> students = studentRepository.findAll();

            if (students.isEmpty()) {
                logger.warn("Нет студентов для расчета среднего возраста");
                return 0.0;
            }

            double averageAge = students.stream()
                    .mapToInt(Student::getAge)        // Преобразуем в IntStream возрастов
                    .average()                        // Вычисляем среднее значение
                    .orElse(0.0);               // Если нет данных, возвращаем 0.0

            logger.info("Средний возраст студентов через Stream API: {}.2f", averageAge);
            logger.debug("Всего студентов: {}, суммарный возраст: {}",
                    students.size(),
                    students.stream().mapToInt(Student::getAge).sum());

            return averageAge;
        } catch (Exception e) {
            logger.error("Ошибка при расчете среднего возраста через Stream API: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    // Вычислить сумму чисел от 1 до 1,000,000
    public long calculateSumOptimized() {
        logger.info("Начало вычисления суммы чисел от 1 до 1,000,000");

        long startTime = System.currentTimeMillis();

        try {
            // Способ 1: Использование формулы суммы арифметической прогрессии (самый быстрый)
            // Формула: S = n * (a1 + an) / 2
            long n = 1_000_000L;
            long sum = n * (1L + n) / 2;

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.info("Сумма чисел от 1 до 1,000,000: {}", sum);
            logger.info("Время вычисления: {} мс (оптимизированный метод)", duration);

            return sum;
        } catch (Exception e) {
            logger.error("Ошибка при вычислении суммы: {}", e.getMessage(), e);
            return 0L;
        }
    }

    // Версия 2: Использование parallel stream (параллельная обработка)
    public long calculateSumParallelStream() {
        logger.info("Начало вычисления суммы чисел от 1 до 1,000,000 (parallel stream)");

        long startTime = System.currentTimeMillis();

        try {
            long sum = LongStream.rangeClosed(1, 1_000_000L)
                    .parallel()                         // Включаем параллельную обработку
                    .reduce(0L, Long::sum);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.info("Сумма чисел от 1 до 1,000,000 (parallel): {}", sum);
            logger.info("Время вычисления (parallel): {} мс", duration);

            return sum;
        } catch (Exception e) {
            logger.error("Ошибка при вычислении суммы (parallel): {}", e.getMessage(), e);
            return 0L;
        }
    }

    // Вывода имен студентов в параллельном режиме
    public void printStudentNamesParallel() {
        logger.info("Начало вывода имен студентов в параллельном режиме");

        try {
            // Получаем всех студентов
            List<Student> allStudents = studentRepository.findAll();

            if (allStudents.isEmpty()) {
                logger.warn("В базе данных нет студентов для вывода");
                System.out.println("В базе данных нет студентов");
                return;
            }

            // Берем первые 6 студентов (или меньше, если студентов меньше)
            List<String> studentNames = allStudents.stream()
                    .limit(6)
                    .map(Student::getName)
                    .collect(Collectors.toList());

            logger.info("Будет выведено {} имен студентов", studentNames.size());

            // Вывод первых двух имен в основном потоке
            if (studentNames.size() > 0) {
                System.out.println(studentNames.get(0) + " (основной поток)");
            }
            if (studentNames.size() > 1) {
                System.out.println(studentNames.get(1) + " (основной поток)");
            }

            // Вывод третьего и четвертого имен в параллельном потоке
            if (studentNames.size() > 2) {
                Thread thread1 = new Thread(() -> {
                    if (studentNames.size() > 2) {
                        System.out.println(studentNames.get(2) + " (параллельный поток 1)");
                    }
                    if (studentNames.size() > 3) {
                        System.out.println(studentNames.get(3) + " (параллельный поток 1)");
                    }
                });
                thread1.start();

                // Даем потоку немного времени на выполнение
                try {
                    thread1.join(100);
                } catch (InterruptedException e) {
                    logger.error("Ошибка при ожидании потока 1: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }

            // Вывод пятого и шестого имен в еще одном параллельном потоке
            if (studentNames.size() > 4) {
                Thread thread2 = new Thread(() -> {
                    if (studentNames.size() > 4) {
                        System.out.println(studentNames.get(4) + " (параллельный поток 2)");
                    }
                    if (studentNames.size() > 5) {
                        System.out.println(studentNames.get(5) + " (параллельный поток 2)");
                    }
                });
                thread2.start();

                // Даем потоку немного времени на выполнение
                try {
                    thread2.join(100);
                } catch (InterruptedException e) {
                    logger.error("Ошибка при ожидании потока 2: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }

            logger.info("Завершение вывода имен студентов в параллельном режиме");

        } catch (Exception e) {
            logger.error("Ошибка при выводе имен студентов в параллельном режиме: {}", e.getMessage(), e);
        }
    }

    // Синхронизированный метод для вывода имени студента в консоль
    private synchronized void printNameSynchronized(String name, String threadName) {
        System.out.println(name + " (" + threadName + ")");
    }

    // Вывода имен студентов с синхронизацией
    public void printStudentNamesSynchronized() {
        logger.info("Начало вывода имен студентов с синхронизацией");

        try {
            // Получаем всех студентов
            List<Student> allStudents = studentRepository.findAll();

            if (allStudents.isEmpty()) {
                logger.warn("В базе данных нет студентов для вывода");
                System.out.println("В базе данных нет студентов");
                return;
            }

            // Берем первые 6 студентов (или меньше, если студентов меньше)
            List<String> studentNames = allStudents.stream()
                    .limit(6)
                    .map(Student::getName)
                    .collect(Collectors.toList());

            logger.info("Будет выведено {} имен студентов", studentNames.size());

            // Вывод первых двух имен в основном потоке
            if (studentNames.size() > 0) {
                printNameSynchronized(studentNames.get(0), "основной поток");
            }
            if (studentNames.size() > 1) {
                printNameSynchronized(studentNames.get(1), "основной поток");
            }

            // Вывод третьего и четвертого имен в параллельном потоке
            if (studentNames.size() > 2) {
                Thread thread1 = new Thread(() -> {
                    if (studentNames.size() > 2) {
                        printNameSynchronized(studentNames.get(2), "параллельный поток 1");
                    }
                    if (studentNames.size() > 3) {
                        printNameSynchronized(studentNames.get(3), "параллельный поток 1");
                    }
                });
                thread1.start();

                // Даем потоку немного времени на выполнение
                try {
                    thread1.join(100);
                } catch (InterruptedException e) {
                    logger.error("Ошибка при ожидании потока 1: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }

            // Вывод пятого и шестого имен в еще одном параллельном потоке
            if (studentNames.size() > 4) {
                Thread thread2 = new Thread(() -> {
                    if (studentNames.size() > 4) {
                        printNameSynchronized(studentNames.get(4), "параллельный поток 2");
                    }
                    if (studentNames.size() > 5) {
                        printNameSynchronized(studentNames.get(5), "параллельный поток 2");
                    }
                });
                thread2.start();

                // Даем потоку немного времени на выполнение
                try {
                    thread2.join(100);
                } catch (InterruptedException e) {
                    logger.error("Ошибка при ожидании потока 2: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }

            logger.info("Завершение вывода имен студентов с синхронизацией");

        } catch (Exception e) {
            logger.error("Ошибка при выводе имен студентов с синхронизацией: {}", e.getMessage(), e);
        }
    }
}
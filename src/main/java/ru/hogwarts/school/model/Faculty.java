package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;

@Entity
public class Faculty {

    @Id
    @GeneratedValue
    private long    id;     // Идентификатор

    private String  name;   // Имя
    private String  color;  // Цвет факультета

    @OneToMany(mappedBy = "faculty")
    @JsonIgnore
    private Collection<Student> students;

    public Faculty() {}

    public Faculty(String name, String color) {
        this.id       = 0;  // При обращении к базе будет переписано, необходимо объявление, значение не важно.
        this.name     = name;
        this.color    = color;
    }

    public Collection<Student> getStudents() {
        return students;
    }

    public void setStudents(Collection<Student> students) {
        this.students = students;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return id == faculty.id && Objects.equals(name, faculty.name) && Objects.equals(color, faculty.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}

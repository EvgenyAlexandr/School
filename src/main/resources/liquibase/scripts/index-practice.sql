-- liquibase formatted sql

-- changeset evgeniy:1 create_student_name_index
CREATE INDEX idx_student_name ON student(name);

-- changeset evgeniy:2 create_faculty_name_color_index
CREATE INDEX idx_faculty_name_color ON faculty(name, color);